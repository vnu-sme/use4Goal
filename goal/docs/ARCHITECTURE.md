# Kiến trúc GOAL plugin

Tài liệu này quy định tổ chức mã nguồn và chiều phụ thuộc của GOAL plugin. Ngữ
nghĩa DSL, luật dịch và đặc tả hình thức được điều hướng từ [main.md](main.md).

## 1. Nguyên tắc chính

GOAL được tổ chức theo **module tính năng tái sử dụng** và **feature đầu vào**.
Module cung cấp năng lực; feature là điểm vào để người dùng gọi một hoặc nhiều
module.

```text
USE menu/toolbar
       |
       v
feature.<use-case>                 action + form + orchestration
       |
       +----------+---------------+----------------+
       v          v               v                v
   dsl.<name>  translate.*      trace.*          verify.*
                  |               |                |
                  +---------------+----------------+
                                  |
                              dsl.<name>
```

Action tương đương một endpoint trong backend: nhận sự kiện từ USE, tạo feature
và chuyển quyền điều khiển. Action không parse, validate, translate hay chứa
luật miền. Form là delivery adapter của cùng endpoint nên đi cùng feature, không
nằm trong module DSL.

## 2. Cấu trúc package

```text
org.vnu.sme.goal
├── dsl.<name>                    module định nghĩa một ngôn ngữ
│   ├── parser
│   ├── ast
│   ├── mm
│   ├── ocl                       nếu DSL có tích hợp OCL
│   └── view                      mô hình/adapter hiển thị tái sử dụng
├── translate.<source2target>    module dịch tĩnh
├── trace.<name>                 module thực thi theo trạng thái
├── verify.<name>                module kiểm chứng và phối hợp mô hình
├── feature.<use-case>           endpoint của plugin
│   ├── Action...
│   ├── ...Form
│   └── ...Service               orchestration riêng của use case, nếu cần
├── usegen                       hạ tầng sinh USE dùng chung
└── gui                          widget/hạ tầng giao diện dùng chung
```

`feature` được chia theo use case, không chia theo loại kỹ thuật `action`, `gui`
hay `application`. Vì vậy action mới có thể gọi lại các module gốc mà không kéo
theo action/form cũ.

Ví dụ đích cho chức năng mở ACL:

```text
org.vnu.sme.goal.dsl.acl
├── parser
├── ast
├── mm
├── ocl
└── view

org.vnu.sme.goal.feature.openacl
├── ActionOpenAcl.java
├── AclForm.java
└── AclOpenService.java
```

`AclDemoMain` là một adapter CLI, không phải lõi ACL. Khi còn cần, nó đặt tại
`feature.openacl` hoặc một package adapter CLI riêng; không đặt trực tiếp trong
`dsl.acl`.

## 3. Trách nhiệm từng nhóm

### `dsl`

Một DSL chỉ định nghĩa pipeline ngôn ngữ:

```text
source -> parser -> AST -> semantic model -> validator
                                      |
                                      +-> reusable view model/adapter
```

- Parser chỉ nhận cú pháp và tạo AST/MM qua factory.
- `ast` giữ cấu trúc gần mã nguồn.
- `mm` giữ mô hình ngữ nghĩa và validator tĩnh.
- `ocl` tích hợp OCL ở mức module, không biết tới form hay action.
- `view` chỉ giữ biểu diễn có thể được nhiều feature dùng lại. Dialog, file
  chooser, preferences và thông báo lỗi thuộc `feature`.

### `translate`

Translator nhận semantic model qua API công khai, không đọc parse tree và không
phụ thuộc GUI. Translator phải báo lỗi với construct chưa hỗ trợ; không được bỏ
qua hoặc xấp xỉ ngầm.

### `trace`

Trace làm việc với object state và checkpoint cụ thể. Nó cung cấp API thực thi,
step và đánh giá; cửa sổ điều khiển trace thuộc feature tương ứng.

### `verify`

Verifier phối hợp API công khai của DSL, translator và trace. Nó không sao chép
parser hoặc luật ngữ nghĩa. Workspace/form kiểm chứng thuộc feature.

### `feature`

Mỗi package là một use case hoàn chỉnh ở biên hệ thống, ví dụ `openacl`,
`exportacltouse`, `verifyconformance`.

- `Action...` là entry point được khai báo trong `useplugin.xml` và phải mỏng.
- `...Form` quản lý input, trạng thái UI và thông báo cho người dùng.
- `...Service` phối hợp module khi use case đủ phức tạp; đây không phải domain
  service và không được các module lõi gọi ngược lại.
- Hai feature có thể dùng cùng module, nhưng không gọi action của nhau. Phần
  logic dùng chung phải được đưa xuống API module hoặc một application component
  không phụ thuộc UI.

## 4. Chiều phụ thuộc bắt buộc

```text
feature -> verify/trace/translate -> dsl
feature -------------------------> dsl
```

- `dsl` không import `feature`, USE plugin action hay Swing form.
- `translate`, `trace`, `verify` không import `feature` hay form.
- Feature được phép phụ thuộc nhiều module để hoàn thành một use case.
- `useplugin.xml` chỉ trỏ tới class trong `feature.*`.
- Không đặt package `action`, `gui` hoặc `application` bên trong `dsl.*`,
  `translate.*`, `trace.*` hay `verify.*`.
- `org.vnu.sme.goal.gui` chỉ chứa thành phần UI thật sự dùng chung; không chứa
  workflow riêng của một action.

## 5. Luồng chuẩn của một endpoint

```text
Action -> Form -> feature service -> module API -> Result
                  ^                              |
                  +---------- render ------------+
```

Với Open ACL:

1. `ActionOpenAcl` nhận `MainWindow` và mở `AclForm` trên EDT.
2. `AclForm` lấy đường dẫn và lựa chọn hiển thị.
3. `AclOpenService` gọi `AclCompiler` và API view của module ACL.
4. Service trả `Result`; form chỉ hiển thị kết quả.

Một endpoint CLI, test fixture hoặc action mới có thể gọi thẳng `AclCompiler`,
validator và view API mà không phụ thuộc `ActionOpenAcl` hay `AclForm`.

## 6. Quy tắc mở rộng

Khi thêm một action mới:

1. xác định module/API hiện có mà use case cần;
2. bổ sung API module nếu năng lực dùng chung còn thiếu;
3. tạo `feature.<use-case>` chứa action và form của endpoint;
4. chỉ đăng ký action trong `useplugin.xml`;
5. kiểm tra không có phụ thuộc ngược từ module về feature.

Một construct DSL chỉ được coi là hỗ trợ khi đã có, nếu phù hợp:

1. grammar và AST;
2. semantic model và static validator;
3. operational/formal semantics;
4. translator hoặc trace evaluator;
5. reusable view;
6. ví dụ và kiểm thử;
7. tài liệu trong cây ở `main.md`.

Parser chấp nhận một keyword không đồng nghĩa plugin đã hỗ trợ ngữ nghĩa của
keyword đó.

## 7. Lộ trình chuyển đổi

Để giữ các thay đổi dễ kiểm tra, chuyển từng feature theo lát dọc:

1. dùng `openacl` làm mẫu: chuyển `ActionOpenAcl`, `AclForm`, `AclOpenService`
   và cập nhật `useplugin.xml`;
2. chuyển lần lượt các viewer DSL khác;
3. chuyển các action của translator, trace và verifier;
4. thêm architecture test cấm module import `feature`, Swing form và
   `IPluginActionDelegate`;
5. xóa các package `*/action`, `*/gui`, `*/application` cũ sau khi mọi tham
   chiếu đã được chuyển.

Không thực hiện một lần đổi package toàn bộ repository: mỗi lát dọc phải compile
và test độc lập để tránh trộn thay đổi kiến trúc với thay đổi ngữ nghĩa.

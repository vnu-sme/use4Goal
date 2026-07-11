# Phương pháp vẽ iStar Scenario Diagram (instance-level) cho một kịch bản cụ thể

Ngày lập: 2026-07-10

Tài liệu này rút ra từ việc đọc
[`09-istar-mtg-scenario-diagram.drawio`](drawio/09-istar-mtg-scenario-diagram.drawio) —
đối chiếu với domain model
[`mtg.istar`](../goal/src/main/resources/examples/mtg/mtg.istar), kịch bản gốc
[`mtg.iscn`](../goal/src/main/resources/examples/mtg/mtg.iscn) (3 participant: xing,
amr, naya) và kịch bản mở rộng
[`meeting_five_participants_two_secretaries.iscn`](../goal/src/main/resources/examples/mtg/meeting_five_participants_two_secretaries.iscn)
(5 participant + 2 secretary — xing, amr, naya, charlie, bao / alex, hung) — để trả lời
câu hỏi: **khi đã có domain model type-level (`.istar`) + một kịch bản instance-level
(`.iscn`), thì vẽ diagram SR-instance (`.drawio`) cho kịch bản đó theo quy tắc nào?**

Không lặp lại khái niệm domain model vs kịch bản (xem
[`concept-domain-model-vs-scenario.md`](concept-domain-model-vs-scenario.md)) hay ký
hiệu concrete syntax type-level (xem [`istar-notation.md`](istar-notation.md)). Tài
liệu này chỉ nói về **diagram của một kịch bản (instance-level)**, khác diagram gốc
[`08-istar-mtg-diagram.drawio`](drawio/08-istar-mtg-diagram.drawio) ở chỗ: mọi node bây
giờ là *instance*, có thể lặp lại theo số lượng instance, và mỗi node còn mang thêm
**trạng thái đã đạt được** trong kịch bản đó.

## 1. Nguồn dữ liệu bắt buộc phải có trước khi vẽ

Không vẽ "tự do" — mọi node/edge trong diagram phải truy được về đúng 1 trong 3 nguồn:

1. **Từ vựng** (loại intentional element nào tồn tại, quan hệ refine/contribute/depend
   nào giữa chúng) → lấy từ `.istar` type-level, không được bịa thêm phần tử mới.
2. **Cast + trạng thái** (instance nào tồn tại, `fire`/`assign` nào đã xảy ra trên
   instance nào) → lấy từ `.iscn`.
3. **Câu hỏi tổng hợp** (`aggregate ... all/any of Type over Element`) → quyết định
   nhãn "True (via ...)" / "Fulfilled (AND of N participants)" viết trên node, không
   cần vẽ thêm node aggregate riêng.

Nếu thiếu `.iscn`, việc đầu tiên là viết nó ra (theo cấu trúc 3 phần ở
`concept-domain-model-vs-scenario.md` §1.3) — diagram luôn là bản vẽ *theo sau* file
kịch bản, không phải ngược lại.

## 2. Boundary: một khung cho mỗi ACTOR INSTANCE, không phải mỗi Role

Type-level chỉ có 4 khung (Initiator, Organizer, Secretary, Participant). Ở
instance-level, mỗi role có N instance trong `.iscn` thì vẽ N khung riêng:

```
b_init  = "Initiator  [abdul]"
b_org   = "Organizer  [matilda]"
b_sec   = "Secretary  [alex]"
b_xing  = "Participant  [xing]  — hasCalendar"
b_amr   = "Participant  [amr]  — hasCalendar"
b_naya  = "Participant  [naya]  — no calendar"
```

Ở kịch bản 2 secretary, thêm `b_hung = "Secretary [hung] — canCall(charlie,bao), alex
cannot"` và `b_charlie`, `b_bao` — **domain model `mtg.istar` không đổi một dòng nào**,
chỉ số khung Participant/Secretary tăng theo cast của `.iscn` đó.

Quy tắc đặt tên nhãn khung: `<Role> [<instance-name>] — <fact quyết định nhánh OR của
instance này>`. Ghi thẳng fact quyết định (hasCalendar, canCall, no calendar) lên nhãn
khung, không chỉ để trong `.iscn` — người đọc diagram không cần mở file text song song
mới hiểu vì sao instance đó rẽ nhánh khác instance kia.

## 3. Singleton vs per-instance: quyết định bằng "phần tử này có nằm dưới role đa
   instance không"

Với mỗi intentional element trong `.istar`, chỉ có 2 khả năng ở scenario diagram:

- **Vẽ đúng 1 lần** — nếu element thuộc role chỉ có 1 instance trong kịch bản này
  (`HaveMeetingOrganized`, `DecideMeetingDetails`, `HaveSchedulingPerformed`,
  `TimetablesCollected`, `ChooseTimeAndDate`, `MeetingAnnounced`,
  `HaveMeetingScheduled`, `Inclusivity`, `QuickScheduling` — tất cả thuộc
  Initiator/Organizer, luôn singleton).
- **Vẽ N lần, mỗi lần gắn `(tên-instance)` vào cả id lẫn nhãn** — nếu element thuộc
  role có nhiều instance trong kịch bản (`TimetableCollected`, `HavePPCalled`,
  `CollectFromCalendar`, `HaveSecretaryCallPP`, `AnnounceMeeting`,
  `MeetingAttendedByParticipant`, `Participate`, `CollectConstraintsByPhone` — thuộc
  Participant hoặc Secretary).

Ví dụ id: `i_TC_xing`, `i_TC_amr`, `i_TC_naya` (3 bản của `TimetableCollected`) —
không phải 1 node `TimetableCollected` dùng chung.

## 4. Quy tắc dịch refinement: OR type-level + multiplicity → AND scenario-level

Đây là điểm dễ hiểu sai nhất, và là phát hiện chính rút ra từ diagram này.

`mtg.istar` đánh dấu `TimetableCollected`, `MeetingAttendedByParticipant`,
`AnnounceMeeting`, `HaveSecretaryCallPP`, `HaveSchedulingPerformedByOrganizer` là **`or`**
— không phải vì về nghĩa nó là OR, mà vì compiler bắt AND-refine phải có ≥ 2 con tĩnh, và
type-level chỉ có 1 con đại diện (xem comment đầu `mtg.istar`, dòng 17–28).

Khi xuống scenario với N instance thật, N bản instance của con đó tồn tại đồng thời —
và câu hỏi thật ở `.iscn` là `aggregate ... all of Participant over X`, tức **AND của
N nhánh**, không phải OR. Diagram phản ánh đúng ngữ nghĩa đó, không sao chép máy móc
từ khoá `or` của type-level:

```
i_TC_xing ─AND→ i_TimetablesCollected
i_TC_amr  ─AND→ i_TimetablesCollected      (e_tc1..e_tc5, edge "AND")
i_TC_naya ─AND→ i_TimetablesCollected
```

Cùng quy tắc cho `i_MAP_*  ─AND→ i_MeetingAttended` và `i_AM_*  ─AND→
i_MeetingAnnounced`. Nói ngắn: **OR type-level (do ràng buộc cú pháp, không do ngữ
nghĩa) + N instance ở scenario = AND-fan-in N nhánh ở scenario diagram.**

Ngược lại, **OR thật** (một instance đơn lẻ tự chọn 1-trong-2 đường) vẫn giữ nguyên
OR ở scenario level — ví dụ trong nội bộ `naya`:

```
i_CCBP_naya ─OR→ i_HavePPCalled_naya ─OR→ i_TC_naya
```

naya chỉ đi đúng 1 nhánh (điện thoại), không phải cả 2 — đây là OR thật, giữ nguyên
khoá OR từ type-level.

## 5. Màu = trạng thái, không phải chú thích rời

Palette dùng xuyên suốt diagram (ghi thẳng trong node `legend` trên canvas, không cần
tài liệu ngoài):

| Màu | Ý nghĩa |
|---|---|
| Xanh lá (`#d5e8d4` / viền `#82b366`) | Fulfilled / True — instance này đã đạt trạng thái đó trong trace của `.iscn` |
| Xám (`#f5f5f5` / viền `#999999`, chữ xám) | Nhánh OR **tồn tại ở type-level nhưng instance này không đi qua** — không phải lỗi, chỉ là UNKNOWN vì không được `fire`/`assign` |
| Cam (dự trữ, chưa dùng trong kịch bản này) | Pending |

Hệ quả quan trọng: **cả 2 nhánh của một OR thật đều được vẽ ra**, kể cả nhánh không
được chọn — ví dụ `i_HavePPCalled_xing` (xám) tồn tại song song với
`i_CFC_xing`→`i_TC_xing` (xanh), dù xing dùng calendar chứ không gọi điện. Vẽ cả nhánh
xám để người đọc thấy "đây là 1 trong 2 khả năng type-level cho phép, khả năng kia
không được kịch bản này dùng tới" — chỉ tô hết xanh mà bỏ nhánh không dùng sẽ khiến
diagram trông như type-level chỉ có 1 đường, đánh mất thông tin OR gốc.

## 6. `depend` (SD-edge) instantiate theo đúng cặp instance, không theo role

`mtg.istar` khai báo dependency ở mức role (`depend
Initiator.MeetingAttendedByParticipant -> task Participate -> Participant`). Ở
scenario, edge này nhân bản theo từng cặp instance **cùng một participant cụ thể**,
không nối chéo:

```
i_MAP_xing  ─depend(assign)→ i_Participate_xing
i_MAP_amr   ─depend(assign)→ i_Participate_amr
i_MAP_naya  ─depend(assign)→ i_Participate_naya
```

không phải `i_MAP_xing → i_Participate_amr`. Điều này khớp với cách `.iscn` gắn
`assign xing.MeetingAttendedByParticipant = Fulfilled` — luôn cùng 1 tên instance ở 2
vế.

## 7. `make`/`help`/... tới quality: đích vẫn singleton, nguồn là nhiều instance,
   nhãn ghi rõ "via ai"

`Inclusivity` và `QuickScheduling` chỉ có 1 node (thuộc Organizer, singleton) dù nhiều
participant cùng góp vào nó qua `make`. Thay vì vẽ thêm 1 node aggregate riêng cho
"any/all of Participant over Quality" (đúng như `.iscn` khai báo ở dòng
`aggregate SomeInclusivityViaSecretary : any of Participant over Inclusivity`), diagram
này viết thẳng kết quả tổng hợp vào nhãn của chính node quality:

```
i_Inclusivity     value="Inclusivity\nTrue (via naya)"
i_QuickScheduling value="QuickScheduling\nTrue (via xing, amr)"
```

Cách này rẻ hơn (không cần thêm shape/edge cho aggregate) và vẫn trả lời đúng câu hỏi
`any`/`all` — miễn nhãn liệt kê đúng tập instance đã kích hoạt qua edge `make` màu tím
nét đứt.

## 8. Khi mở rộng multiplicity (thêm participant/secretary): chỉ lặp lại quy tắc trên,
   không sửa domain model

Kịch bản `meeting_five_participants_two_secretaries.iscn` (5 participant + 2
secretary) chứng minh trực tiếp luận điểm ở
`concept-domain-model-vs-scenario.md`: multiplicity là chuyện của `.iscn`/diagram, không
phải của `.istar`. Khi mở rộng cast, chỉ cần:

1. Thêm khung actor instance mới (`b_charlie`, `b_bao`, `b_hung`).
2. Thêm N bản mới của các element per-instance (§3), theo đúng fact riêng của instance
   đó (`b_charlie` ghi "no calendar, alex cannot call" ngay trên khung).
3. Nối các AND-fan-in (§4) và depend (§6) theo instance mới — số nhánh AND tăng từ 3
   lên 5, cấu trúc quan hệ không đổi.
4. Không sửa bất kỳ node/edge nào thuộc phần singleton (Initiator/Organizer) —
   đó là bằng chứng trực quan rằng domain model không cần đổi.

Nếu phát hiện meta-finding này đáng ghi chú (như file 09 đã làm ở node `note2`), thêm
1 text node ngắn trên canvas nói rõ: kịch bản nào, thêm gì, và khẳng định domain model
không đổi — để người đọc diagram không phải suy luận lại từ đầu.

## 9. Tóm tắt quy trình (checklist khi vẽ 1 scenario diagram mới)

1. Xác định `.istar` (từ vựng) + `.iscn` (cast, fire/assign, aggregate) đã có sẵn.
2. 1 khung/actor-instance, nhãn ghi kèm fact quyết định nhánh OR của instance đó.
3. Với mỗi element type-level: element thuộc role singleton → 1 node; thuộc role đa
   instance → N node, đặt tên `<Element>_<instance>`.
4. OR type-level (do ràng buộc ≥2-con) trên role đa-instance → AND-fan-in N nhánh ở
   scenario, theo đúng `aggregate all of Type over Element`. OR thật (1 instance tự
   chọn nhánh) → giữ OR, vẽ cả nhánh không dùng màu xám.
5. Tô theo `fire`/`assign` trong `.iscn`: xanh = Fulfilled/True đạt được, xám = nhánh
   type-level cho phép nhưng instance này không đi qua.
6. `depend` nhân bản theo đúng cặp instance cùng tên ở 2 vế, không nối chéo giữa các
   instance khác nhau.
7. Contribution tới quality: giữ đích singleton, nhiều nguồn per-instance, viết kết quả
   `aggregate any/all` thẳng vào nhãn node quality thay vì vẽ thêm node.
8. Thêm legend màu ngay trên canvas; thêm text note nếu diagram đang minh hoạ một
   meta-finding (ví dụ: mở rộng multiplicity không cần sửa domain model).

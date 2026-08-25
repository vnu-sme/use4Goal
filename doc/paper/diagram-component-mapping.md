# Quy ước component và ký hiệu cho ACL, iStar và BPMN

## Nguyên tắc

View của ba ngôn ngữ dùng chung hạ tầng diagram của USE: `DiagramView`,
`DiagramGraph`, `PlaceableNode`, `EdgeBase`, waypoint, selection, lưu/khôi phục
layout, màu lấy từ `DiagramOptions` và factory vẽ đầu mũi tên. Component chuyên biệt
của USE chỉ được kế thừa khi khái niệm nguồn có cùng cấu trúc và hành vi. Hình dáng
gần giống nhau không đủ để coi hai khái niệm là cùng một component.

## Component diagram có sẵn trong USE

| Nhóm | Component USE | Khả năng tái sử dụng |
|---|---|---|
| Khung diagram | `DiagramView`, `DiagramGraph` | Zoom/pan, selection, popup, layout, repaint và persistence |
| Node cơ sở | `PlaceableNode` | Vị trí, kích thước, chọn, resize, hit area và persistence |
| Node có ngăn | `CompartmentNode` | Tính/dựng danh sách thuộc tính hoặc literal theo các ngăn |
| UML classifier | `ClassifierNode` | Tên, attribute, operation và phản ứng với `DiagramOptions` |
| UML class | `ClassNode` | Class/abstract class và các ngăn UML chuẩn |
| UML enum | `EnumNode` | Stereotype enumeration và literal; constructor hiện không public ngoài package USE |
| UML datatype/signal | `DataTypeNode`, `SignalNode` | Chỉ dành cho classifier UML tương ứng |
| Object | `ObjectNode` | Object và slot của object diagram, không phải metamodel node |
| Actor | `ActorNode` | Actor của communication diagram; không có actor boundary iStar |
| State | `StateNode`, `PseudoStateNode`, `FinalStateNode` | State machine, gắn với metamodel state machine của USE |
| Edge cơ sở | `EdgeBase`, `SimpleEdge` | Waypoint, reflexive edge, selection và persistence |
| UML edge | `GeneralizationEdge`, `BinaryAssociationOrLinkEdge` | Phụ thuộc trực tiếp vào `MGeneralization`/`MAssociation` của USE |
| Edge property | `AssociationName`, `Multiplicity`, `Rolename` | Nhãn association và multiplicity có chiến lược định vị sẵn |
| Hình/mũi tên | `DirectedEdgeFactory`, `DirectedGraphicFactory` | Mũi tên đặc/rỗng và primitive có hướng |

## Bảng quyết định node

| Ngôn ngữ | Concept | Component kế thừa | Ký hiệu được chốt | Lý do |
|---|---|---|---|---|
| ACL | Entity | `ClassNode` | UML class chuẩn, attribute trong cùng node | Entity có đúng ngữ nghĩa classifier và phải giữ nguyên giao diện class của USE |
| ACL | Role | `CompartmentNode` | Hộp bo góc, icon người nhỏ trong header, attribute ở ngăn dưới | Có attribute như classifier nhưng Role không phải `MClass` trong view ACL |
| ACL | Group | `CompartmentNode` | Folder/tab, tên ở header, attribute ở ngăn dưới | Giữ ký hiệu Group riêng nhưng tái sử dụng hành vi compartment |
| ACL | Enumeration | `CompartmentNode` | UML enumeration và danh sách literal | `EnumNode` phù hợp về hình nhưng constructor không thể dùng ngoài package; không tạo enum giả tách khỏi model |
| iStar | Agent/Role actor | `PlaceableNode` | Actor boundary tròn; Role có icon người, Agent có nhãn loại | `ActorNode` của communication diagram không hỗ trợ boundary và intentional elements |
| iStar | Goal | `PlaceableNode` | Oval | Ký hiệu chuẩn iStar, không đồng nghĩa UML state |
| iStar | Task | `PlaceableNode` | Hình chữ nhật bo góc | Không dùng `StateNode`: task không phải UML state |
| iStar | Resource | `PlaceableNode` | Hình chữ nhật | Resource intentional element, không phải UML object/class |
| iStar | Quality | `PlaceableNode` | Cloud | Ký hiệu chuẩn iStar |
| BPMN | Start/Intermediate/End event | `PlaceableNode` | Một vòng/tròn kép/viền đậm theo BPMN | `PseudoStateNode` gần hình nhưng sai marker và semantics |
| BPMN | Task | `PlaceableNode` | Rounded rectangle | Task BPMN không phải UML state |
| BPMN | Call activity | `PlaceableNode` | Rounded rectangle viền đậm | Ký hiệu BPMN chuẩn |
| BPMN | Subprocess | `PlaceableNode` | Rounded rectangle với marker `+` | Cần marker và containment BPMN riêng |
| BPMN | Gateway | `PlaceableNode` | Hình thoi, marker XOR/AND/OR/event-based | Không dùng `DiamondNode`: lớp đó dành cho n-ary UML association |
| BPMN | Pool/Lane | `PlaceableNode` | Container chữ nhật, header dọc | USE không có swimlane component dùng chung |
| BPMN | Choreography | `PlaceableNode` | Activity với participant band | Ký hiệu BPMN riêng |

## Bảng quyết định quan hệ

| Ngôn ngữ | Quan hệ | Component cơ sở | Nét và đầu mút |
|---|---|---|---|
| ACL | Generalization | `EdgeBase` + arrow primitive USE | Nét liền, tam giác rỗng ở superclass |
| ACL | Association | `EdgeBase` | Nét liền, không marker; tên giữa, multiplicity hai đầu |
| ACL | Aggregation | `EdgeBase` | Nét liền, thoi rỗng ở owner/source |
| ACL | Composition | `EdgeBase` | Nét liền, thoi đặc ở owner/source |
| ACL | Owner | `EdgeBase` | Nét liền, vuông rỗng nhỏ ở Group cha; không dùng thoi composition |
| ACL | Compatibility intra-group | `EdgeBase` | Nét liền, vòng tròn rỗng ở Role; hai đầu nếu bidirectional |
| ACL | Compatibility inter-group | `EdgeBase` | Nét đứt, cùng marker compatibility |
| iStar | Refinement | `EdgeBase` | Nét liền hướng child tới parent; nhãn AND/OR |
| iStar | Contribution | `EdgeBase` | Nét liền có mũi tên; nhãn contribution |
| iStar | Dependency | `EdgeBase` | Hai đoạn dependency qua dependum, badge `D` |
| iStar | Qualification | `EdgeBase` | Nét đứt để phân biệt constraint relation |
| BPMN | Sequence flow | `EdgeBase` + `DirectedEdgeFactory` | Nét liền, mũi tên đặc |
| BPMN | Message flow | `EdgeBase` + `DirectedEdgeFactory` | Nét đứt, mũi tên rỗng; marker tròn rỗng ở đầu gửi |

## Quy tắc thống nhất giao diện

- Màu selection, frame, text và edge lấy từ `DiagramOptions`; màu nền theo ngôn ngữ
  chỉ là sắc nhạt, không thay đổi semantics.
- Font tên node dùng font diagram của USE; nhãn edge và detail dùng cùng một cỡ chung.
- Stroke thường, stroke nhấn mạnh và mẫu dash được khai báo tập trung, không lặp
  magic number trong ba view.
- Marker chỉ mang một nghĩa trong cùng một view. Đặc biệt ACL Owner không dùng thoi,
  để không trùng Aggregation/Composition.
- Badge trạng thái chạy là lớp trang trí; không thay hình cơ sở của concept.
- Không chuyển đổi metamodel chỉ để vẽ. Adapter sang `MClass` chỉ dùng cho ACL Entity,
  là concept đã được quyết định biểu diễn như UML class.

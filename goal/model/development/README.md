# ACL/iStar state-aware development package

Gói này là candidate 3.0 được phát triển bottom-up từ M0; nó không thay thế
ngay các metamodel 2.0 hoặc compiler Java hiện hành.

## Files

- `acl-state.ecore`: schema ACL và system-history runtime metamodel.
- `istar-state.ecore`: intentional specification và goal-trace metamodel.
- `mtg-development.acl.xmi`: ACL M1 example.
- `mtg-development.istar.xmi`: iStar M1 example tham chiếu Actor của ACL.
- `mtg-development.state.xmi`: M0 identities cùng state `s0`, `s1`.
- `mtg-development.goaltrace.xmi`: full iStar reevaluation trên `s0`, `s1`.

Tài liệu ngữ nghĩa:

- `../../docs/semantics/development/acl-state-model.md`
- `../../docs/semantics/development/istar-state-model.md`
- `../../docs/semantics/development/mtg-stateful-example.md`

## Compatibility boundary

Namespace mới:

```text
https://vnu.edu.vn/sme/goal/acl/state/3.0
https://vnu.edu.vn/sme/goal/istar/state/3.0
```

Việc dùng namespace riêng ngăn Eclipse nhầm model phát triển với model 2.0.
Migration parser/compiler chỉ nên bắt đầu sau khi các invariant và ví dụ M0
được chấp nhận.

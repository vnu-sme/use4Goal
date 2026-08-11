# Test cases chuẩn cho Role play-chain và Group scope

Các case này kiểm chứng ngữ nghĩa trong `goal/docs/semantics/dsl/acl.md` và
luật R7, R8, R11, R12 của `ACL_TO_USE_OCL_CASES.md`.

## Ma trận kết quả

| Case | Schema | Kết quả bắt buộc |
|---|---|---|
| 1 | Role gốc không Owner | hợp lệ; sinh `Agent_plays_R` |
| 2 | Role con, Role cha không Owner | hợp lệ; sinh `Parent_plays_Child` |
| 3 | Role cha/con cùng Group | hợp lệ |
| 4 | Group Role cha là cha trực tiếp của Group Role con | hợp lệ |
| 5 | Group Role cha là tổ tiên nhiều cấp của Group Role con | hợp lệ |
| 6 | Role cha không Owner, Role con có Owner | hợp lệ |
| 7 | Role cha có Owner, Role con không Owner | từ chối |
| 8 | Role cha/con thuộc hai cây Group không liên quan | từ chối |
| 9 | Role trung gian không Owner nhưng tổ tiên có Owner không phù hợp | từ chối |
| 10 | Role specialization cycle | từ chối |
| 11 | Group Owner cycle | từ chối |
| 12 | Hai Group type đúng nhưng occurrence tổ tiên không khớp | OCL `RoleOwnerScope` loại state |

## Case 5 — Group tổ tiên gián tiếp

```acl
acl v2.0 ValidIndirectAncestor {
  role Employee;
  role Manager extends Employee;

  group Company {
    Employee [0..*];
    Division [0..*];
  }
  group Division { Department [0..*]; }
  group Department { Manager [0..*]; }
}
```

Phải sinh:

```use
association Employee_plays_Manager between
  Employee[1] role source_Employee_plays_Manager
  Manager[0..*] role target_Employee_plays_Manager
end

context Manager inv RoleOwnerScope_Employee_Manager:
  self.source_Employee_plays_Manager.source_Employee_in_Company =
  self.source_Manager_in_Department
      .source_Owner_Division_Department
      .source_Owner_Company_Division
```

## Case 7 — Child scope rộng hơn parent scope

```acl
acl v2.0 InvalidWidening {
  role Employee;
  role Manager extends Employee;
  group Company { Employee [0..*]; }
}
```

Phải từ chối với diagnostic chứa:

```text
Role 'Manager' specializes owned Role 'Employee' ... but has no Owner
```

## Case 8 — Hai cây Group không liên quan

```acl
acl v2.0 InvalidUnrelatedGroups {
  role Employee;
  role Manager extends Employee;
  group CompanyA { Employee [0..*]; }
  group CompanyB { Manager [0..*]; }
}
```

Phải từ chối với diagnostic chứa `invalid Role parent`, tên hai Role và hai
Group liên quan.

## Case 9 — Phải xét toàn bộ Role tổ tiên

```acl
acl v2.0 InvalidHiddenAncestor {
  role Person;
  role Employee extends Person;
  role Manager extends Employee;

  group CompanyA { Person [0..*]; }
  group CompanyB { Manager [0..*]; }
}
```

`Employee` không có Owner không được che khuất lỗi giữa `Manager` và `Person`.
Validator phải duyệt `rparent+`, không chỉ cạnh trực tiếp.

## Case 12 — Type đúng nhưng instance sai

Giả sử có hai Company occurrence `company1`, `company2`. Department
`department1` thuộc `company1`; Manager `manager1` thuộc `department1`; nhưng
Employee cha của `manager1` lại thuộc `company2`. Schema type-level vẫn đúng,
nhưng state sai. `RoleOwnerScope_Employee_Manager` phải đánh giá `false`.

## Kiểm thử tự động

- `AclSemanticValidatorTest` kiểm tra case 3–11 và compile toàn bộ `.acl` trong
  `resources/examples`.
- `Acl2UseCanonicalRulesTest` kiểm tra hình dạng R1–R12, sự xuất hiện của
  `RoleOwnerScope`, và đưa mọi output sinh ra trở lại `USECompiler`.

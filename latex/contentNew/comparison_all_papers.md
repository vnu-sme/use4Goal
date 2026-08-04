# So Sánh Tổng Hợp: 14 Bài Báo — Goal Modeling & Business Process

---

## 1. Bản đồ tổng quan theo chủ đề

```
┌─────────────────────────────────────────────────────────────────┐
│                  GOAL ↔ PROCESS ALIGNMENT                       │
├───────────────────┬───────────────────────┬─────────────────────┤
│   TRANSFORMATION  │      VALIDATION       │   OPTIMIZATION      │
│   (Goal → Process)│  (Kiểm tra nhất quán) │  (Goal reasoning)   │
├───────────────────┼───────────────────────┼─────────────────────┤
│ paper_4 Tropos+   │ paper_3 DL (orchest.) │ paper_10 CGM + OMT  │
│ paper_1 URN       │ paper_13 DL (choreo)  │ paper_7 Evolving    │
│ paper_2 Stra2Bis  │ paper_14 VeMI + OCL   │ paper_9 GOAL MC     │
│ paper_6 URN impr. │ paper_11 GoalBPM+KAOS │                     │
│ paper_8 GoBIS     │                       │                     │
├───────────────────┴───────────────────────┴─────────────────────┤
│                    SURVEY / FRAMEWORK                            │
│ paper_1 URN survey (gần 2 thập kỷ kinh nghiệm)                  │
│ paper_12 Goal-oriented Process Mining (GoPED + GoCC)             │
├──────────────────────────────────────────────────────────────────┤
│                    SELF-ADAPTIVE / RUNTIME                       │
│ paper_5 ActivFORMS (MAPE-K + SMC + online evolution)             │
└──────────────────────────────────────────────────────────────────┘
```

---

## 2. Bảng so sánh chi tiết

| # | Bài báo | Goal Language | Process Language | Kỹ thuật chính | Tự động hóa | Có tool? |
|---|---------|--------------|-----------------|----------------|-------------|---------|
| 1 | Combining Goal & Process (URN survey) | GRL | UCM | Propagation algorithms, alignment rules | Một phần | ✓ jUCMNav |
| 2 | Stra2Bis | LiteStrat | Communication Analysis | 3 transformation guidelines | Thủ công (hướng dẫn) | ✗ |
| 3 | Validation User Intentions | GRL | BPMN | Description Logics (DL) | **Hoàn toàn** | ✓ OWL+Pellet |
| 4 | Align Goals & Business Processes | Tropos | BPMN | 5-bước alignment + value propagation | Một phần | ✗ |
| 5 | ActivFORMS | Goals (MAPE-K) | Timed Automata | SMC + Trusted VM + Online update | **Hoàn toàn** | ✓ Uppaal |
| 6 | Improved URN Construction | GRL | UCM | Bridging mechanism + CNF-Actions | Một phần | ✓ jUCMNav |
| 7 | Formal Reasoning Evolving Goals | Tropos | — | CSP + path-based analysis | **Hoàn toàn** | ✓ BloomingLeaf |
| 8 | GoBIS | i* | Communication Analysis | iStar2ca v2.0 guidelines | Một phần | ✗ |
| 9 | Making MC Feasible for GOAL | GOAL beliefs/goals | Transition system | First-order theory + bijection proof | **Hoàn toàn** | ✓ Python+Uppaal |
| 10 | Multi-Objective CGM | CGM | — | OMT/SMT + lexicographic optimization | **Hoàn toàn** | ✓ CGM-Tool |
| 11 | Relating BPM to KAOS | KAOS | BPMN | Effect annotations + RT-LTL checking | Một phần | ✗ |
| 12 | Goal-oriented Process Mining | GRL/KPI | Event logs/BPMN | LTL checkers + trace filtering | **Một phần** | ✗ (prototype) |
| 13 | Validation Orch & Choreo | GRL | BPMN (orch+choreo) | DL + actor dependency checking | **Hoàn toàn** | ✓ reasoner |
| 14 | Verifying Goal Specs for MDD | i* | Integranova/MDD | OCL Verification Measures | **Hoàn toàn** | ✓ VeMI |

---

## 3. So sánh theo góc nhìn "Vấn đề giải quyết"

### 3.1 Alignment (Căn chỉnh goal ↔ process)
| Bài báo | Cách tiếp cận | Điểm mạnh |
|---------|--------------|-----------|
| paper_4 | 5 bước hệ thống + value propagation | Phát hiện goals bị bỏ sót, activities thừa |
| paper_1 | URN tích hợp GRL+UCM trong một chuẩn | Kiểm tra liên tục khi hệ thống thay đổi |
| paper_11 | GoalBPM + RT-LTL cho BPMN + KAOS | Phát hiện unsatisfied goals khi process tiến hóa |
| paper_2 | Transformation từ strategy model | Đảm bảo microservices = tổ chức structure |
| paper_8 | iStar2ca từ i* → CA | Thực nghiệm chứng minh completeness |

### 3.2 Validation (Kiểm tra nhất quán)
| Bài báo | Phạm vi | Kỹ thuật |
|---------|---------|---------|
| paper_3 | Orchestration only | DL → strong/potential inconsistency |
| paper_13 | Orchestration + **Choreography** | DL + actor dependencies |
| paper_14 | i* → MDD conversion | OCL Verification Measures |

**Nhận xét:** paper_13 là mở rộng trực tiếp của paper_3. Cả hai dùng DL nhưng paper_13 xét thêm inter-process message exchange.

### 3.3 Goal Reasoning (Lập luận trên goal models)
| Bài báo | Loại reasoning | Công cụ |
|---------|---------------|---------|
| paper_7 | Temporal (goals thay đổi theo thời gian) | CSP + JaCoP |
| paper_10 | Multi-objective optimization | OMT + OptiMathSAT |
| paper_9 | Model checking correctness | First-order theory + Uppaal |

---

## 4. Các cặp bài báo liên quan chặt chẽ

| Cặp | Mối quan hệ |
|-----|-----------|
| paper_3 ↔ paper_13 | paper_13 = mở rộng paper_3 sang choreography |
| paper_1 ↔ paper_6 | paper_6 = cải tiến quy trình xây dựng URN trong paper_1 |
| paper_4 ↔ paper_11 | Cùng mục tiêu alignment Tropos/KAOS + BPMN, khác kỹ thuật kiểm chứng |
| paper_3 ↔ paper_14 | Cùng dùng formal methods (DL vs OCL) để validate mapping |
| paper_7 ↔ paper_10 | Cùng về goal reasoning, paper_7 = temporal, paper_10 = multi-objective |

---

## 5. Trục phân tích chính

### Trục 1: Hướng tiếp cận
```
Goal → Process          Process → Goal
(Transformation)        (Mining/Discovery)
paper_2, paper_4,       paper_12 (GoPED)
paper_6, paper_8        paper_11 (GoalBPM as-is analysis)
```

### Trục 2: Mức độ tự động hóa
```
Thủ công (hướng dẫn)   Bán tự động           Hoàn toàn tự động
paper_2, paper_8       paper_1, paper_4,      paper_3, paper_5,
                       paper_6, paper_11      paper_7, paper_9,
                                              paper_10, paper_13, paper_14
```

### Trục 3: Phạm vi thời gian
```
Static (snapshot)                    Dynamic/Temporal
paper_3, paper_4, paper_8,           paper_5 (runtime adaptation)
paper_9, paper_10, paper_11,         paper_7 (evolving over time)
paper_13, paper_14                   paper_12 (event logs = historical)
```

---

## 6. Khoảng trống và hướng nghiên cứu tiềm năng

| Khoảng trống | Bài báo có liên quan | Hướng tiềm năng |
|-------------|---------------------|----------------|
| Validation động (goals thay đổi runtime) | paper_5, paper_7 | Kết hợp ActivFORMS + DL validation |
| Scalability của DL reasoning trên large models | paper_3, paper_13 | Kết hợp OMT của paper_10 |
| Goal-oriented mining với choreography | paper_12, paper_13 | GoPED + choreo inconsistency checking |
| Tự động hóa GoalBPM (paper_11) | paper_11 | Tool + formal semantics |
| Validation URN links (paper_1, paper_6) | paper_3, paper_13 | DL-based validation cho UCM |

---

## 7. Tóm tắt định hướng nghiên cứu chung

Tất cả 14 bài báo cùng nhắm đến một vấn đề trung tâm: **"Làm thế nào để đảm bảo business processes thực sự thỏa mãn strategic goals?"**

Ba nhóm giải pháp chính:
1. **Transformation**: Thiết kế quy trình từ goals ngay từ đầu (paper_2, paper_4, paper_6, paper_8)
2. **Validation**: Kiểm tra hình thức sau khi thiết kế (paper_3, paper_11, paper_13, paper_14)
3. **Reasoning**: Phân tích và tối ưu hóa trên goal models (paper_7, paper_9, paper_10) + runtime adaptation (paper_5)

**Xu hướng**: Tự động hóa ngày càng cao — từ guidelines thủ công (Stra2Bis, GoBIS) đến formal verification hoàn toàn (DL reasoning, OMT, CSP).

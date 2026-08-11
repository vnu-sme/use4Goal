# Đặc tả hình thức BPMN

Một process được xem là labeled transition system:

\[
\mathcal B=(N,F,m_0,Data,\rightarrow),
\]

trong đó `N` là flow node, `F` là sequence flow, `m_0` là token marking ban
đầu và `Data` là ACL state. Một configuration là `(m,Σ)` với `WF_A(Σ)`.

Activity `a` được phép thực hiện khi token đầu vào hiện diện và:

\[
\llbracket pre_a\rrbracket_\Sigma=true.
\]

Phép chuyển:

\[
(m,\Sigma)\xrightarrow{a}(m',\Sigma')
\]

tiêu thụ/sinh token theo BPMN, bảo toàn `WF_A(Σ')` và yêu cầu
`post_a(Σ,Σ')`. Với post thuộc fragment thực thi, `Σ'` được suy ra từ post;
ngoài fragment đó phải có adapter hoặc translator báo unsupported.

- XOR split chọn đúng một outgoing flow có guard đúng, hoặc default khi không
  guard nào đúng.
- AND split sinh token trên mọi outgoing flow.
- AND join chỉ enabled khi có đủ mọi incoming token.
- End event tiêu thụ token và đánh dấu process hoàn tất.

Với execution hữu hạn `π=Σ_0…Σ_n`, kiểm chứng tích hợp tìm phản ví dụ:

\[
Init_A(\Sigma_0)\land
\bigwedge_{i<n}Trans_B(\Sigma_i,\Sigma_{i+1})\land
Complete_B(\Sigma_n)\land\neg\Phi_I(\pi).
\]

Model Validator tự sinh `π` trong scope hữu hạn. `UNSAT` chỉ có nghĩa không có
phản ví dụ trong số object và số snapshot đã giới hạn; nó không phải chứng minh
vô hạn không điều kiện.

# 프로젝트 구조

<pre><code>```
[batch-core (Spring Batch + Quartz)]
    └── 실제 Job 실행 담당
    └── Web 의존성 최소화

[batch-runner (Spring Boot Web)]
    └── REST API로 Job 실행 트리거 (JobLauncher)
    └── DB에서 실행 로그 조회 (Spring Batch 메타테이블 사용)
    └── 실행 결과 모니터링 UI 제공
``` </code></pre>
# 콘서트 예약 서비스

본 프로젝트는 콘서트 예약 서비스로, 사용자가 콘서트를 예약할 수 있는 API 기능을 제공하는 프로젝트입니다.

- **개발 환경 설정**
    - **기본 패키지 구조** : Clean Layered Architecture
    - **기술 스택**
      - **DB ORM** : JPA
      - **Test** : JUnit + AssertJ

---

### 📌 [마일스톤](https://github.com/soumunda8/HH99-CONCERT-RESERVATION-API/milestones)
![마일스톤](https://github.com/user-attachments/assets/3112b81d-3327-4413-b2bd-0ff672e33662)


### 📄 [요구사항분석](https://github.com/soumunda8/HH99-CONCERT-RESERVATION-API/issues/2)
- 대기열 시스템을 구축하고, 예약 서비스는 작업가능한 유저만 수행할 수 있도록 해야합니다.
- 사용자는 좌석예약 시에 미리 충전한 잔액을 이용합니다.
- 좌석 예약 요청시에, 결제가 이루어지지 않더라도 일정 시간동안 다른 유저가 해당 좌석에 접근할 수 없도록 합니다.

- 대기열 시스템
  - 대기열 등록시 대기열 테이블에 상태에 비활성화로 설정
  - 폴링용 api를 구현하여 사용자 대기열 상태 계속 확인
  - 순차적으로 20명씩 대기열 테이블에 상태를 활성화로 변경
  - 대기열 테이블 내 종료 시간이 지나면 상태를 만료로 변경
- 콘서트 시스템
  - 예약 가능한 날짜 이후에 콘서트 내역만 예약 가능
  - 좌석 예약시 좌석 테이블 내 상태를 선점으로 변경하면서 결제 테이블에 예약완료로 상태 등록
  - 좌석 테이블 내 종료 시간이 지나면 상태를 가능으로 변경
- 결제 시스템
  - 결제 테이블 내 상태가 예약완료이면서 사용자의 포인트가 충분하면 결제 진행 후 상태를 결제 완료로 변경하면서 좌석 테이블 내 상태를 완료로 변경
  - 결제 테이블 내 종료 시간이 지나면 상태를 예약취소로 변경하면서 좌석 테이블 내 상태를 가능으로 변경


### 📜 [시퀀스 다이어그램](https://github.com/soumunda8/HH99-CONCERT-RESERVATION-API/issues/1)
- 모든 API 적용한 시퀀스 다이어그램
![sequenceDiagram](https://mermaid.ink/img/pako:eNrFVV9P2lAc_So390kyMIVWwT6QgPDgw5ZN3V_xoYE7JZOW1dZMjQlOXIyyxGUQ_oQuuGVbtmxJFVx40C_Evf0Ou7QFWio87GV9IOXec849597fr3cfpqUMgjzcRq9VJKZRIitsyEIuJQL65AVZyaazeUFUwONtJHtHH6lIRd7hRYlKyYp3YhlRmR1ByUqidzIRT9mj_cUC0aipzgNcKvS6Oql2AP54iS80QBplcvXbgpoYirWX5AF5-4s0fpBP5w4eOTo0jpoD-szKauxBIv7cZynYTKqRiFN69Ri_14nWMU67wKhXiNZ1LkRRfXN3maoX8ZeShbV-tyQpD9ZCDP55TLQaIM0WaRWA8a5lHOpUukmKl0a9vG6B3U7s5CbXkWiQo13oXd0Oc4BAFMQWV5eeJH0jrcG-TM2ExMxgyy2zni0jF7rRKDk8TjoaF951Qh4343Eszjh4uNPe-HrNqNfAzNPY0irAtXN8WnbnN2ONzkHYUsCavfuWRE8v2AxvsmEh9drXpKoD8rlEitqkYGPFUzshlVtA5fHpV6PSdJO9rFFGJ3HIalVwu2PHHZE9jh1NxQ-5luC4XQfUbZkcabQcPfhEPOBZwEI6y2hceRTLbcbsEEC0D_js-q48bnfTJCol_O0GzCR3kKj4pjTQxNaxTr_fOclnD5eW_61zxkvsjBZWEeDvJdNj56TXvgH3ggz-U1z_bx6hH-aQnBOyGfqV3-9PpKCyiXIoBXn6mhHkVymYEg8oTlAVaWVXTENekVXkh7KkbmxC_qWwtU3_qfmMoAzuh-Eo_XBDfh--gXwgGGbmZucWWGZ-PjIfCXOsH-72h0PswizHRebYEMtE2NCBH-5JElVgZ7lgOBxciHBBhmG5YMQPUSarSPJ960oybyZzhRcmvm_q4C9d7sjr?type=png)

### 🗄️ [ERD](https://github.com/soumunda8/HH99-CONCERT-RESERVATION-API/issues/4)
![ERD](https://mermaid.ink/img/pako:eNqlVstu00AU_RVrNqEirZKqSVPvXMdQq3mROBWgSNHUHhKLxA6OXTUkkSoUIYS6YEGlFnURhBBUYlEKKmLRH6rdf2Bm7NSTJ6B6ldznufeeuTNdoJoaAjxAVlqHNQs2K0bF4PBXbiOL6_q_ybcjFMUtoXhvNZFY4hyslDWusB3q5ZzCtUzdsNtcBaSlB0I5o_BcrAJ8kz4bt1oghtUtvW2bVofNQqKQ4IGK5sDxhLKSr8o5sShlpZwyijkH1gxlbImDqq2bhtJpIRwvUi5JHM95r755H84iXI-LELuHVHZ55g0HETYHAaXWoVFDGgU-q6JHDnLQrH6t4dwviHKiX_8MnDqXbGg7pLORkiLk0ptPMFL39-Dm5NQbfL85eU9rEERF3iE1jIulxwW5SMTul0P30-FYaYqclXDEbMFPI1oI2kiw2RGK5WIRN716a7rAX9pv6Zbvz8C-Pj8YgYu4H08598fB9cXVzdGxe_ZzPO_9eMy9HExxRjQNFVl2taTWkeY00CRjVF8_Uv-dNYwT2_a0oEgc3IN6A-42UBHhqexBQps0Rjfu3YT7JYxZNJ0RH0YaCzWhbuhG7Va_-EAExc2p6T9PQOCl6HYDzYxXsHQVTSAgQCfTt7Hsv_oYNn86UM5p7iJrNr2JPmS3sCPIGWEzQ-iKaeO-_UwpvCVl0uRsDobe8B2VpPM5elpPBpOUXniwQr6SvHegO3Fn2B5WQclO8E5TnU05h-kM5yYnYoWqO67FcL6zR8JkCiezmc9vS3QIx2-8o6ug8WQUBUEm8uuLc294yshFISdKGdbn11fv9bz9wyS9w1iYKMx0pgqiQ_Irmh7TDCRzpsVs_l5vedns-rcmj1MGPQfMZcqYTFyAix1YTkxbjtYHMe71plcl8QhXHZizUINUdBMwHsyhngHs1nocERXPwR7QDoAoaCILL0oNvz0o0SvArqMmqgBipkHrOQnXx3bQsc1Sx1ABb1sOigLLdGp1wD-DjTb-57Q0PKHg7TIyaUED8F2wD_hkcmUDf6upeCIV20glUlHQwdKV9UQyubYaX08mYrHEWjzZj4KXpokDxKMAaTqeStZ_F9HnEY34lOpJgv4fP6r6Og?type=png)

### 🔔 [API 명세서](https://github.com/soumunda8/HH99-CONCERT-RESERVATION-API/issues/5)

### 🔘 [mock API](https://github.com/soumunda8/HH99-CONCERT-RESERVATION-API/issues/6)
![Swagger](https://github.com/user-attachments/assets/61171986-b02a-4091-b6ee-15bb3e8d32da)

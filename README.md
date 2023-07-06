# fkream-batch

fkream-batch는 [**FKREAM**](https://github.com/f-lab-edu/FKREAM)에서 생성한 데이터를 배치 처리하기 위해 Kafka, Spring Batch를
활용하는 것이 목표입니다.

## 주요 기능

- Kafka 메시지 큐에 저장된 거래 데이터(ex: `{"itemId": 1, "price": 10000}`)를  하루 단위로 가져와서 전 날 평균 가격을 계산하고 통계 데이터베이스에 저장합니다.
- Kafka 메시지 큐에 저장된 검색 데이터를 1시간 단위로 가져와서 검색 카운트 수를 집계하고, 엘라스틱에 저장합니다.

## 설명

- 개발기간 : 2023.06.01 ~
- 백엔드 : Java 11, SpringBoot, JPA, SpringBatch, Kafka, MySQL, Elastic Search
- Tool : IntelliJ, Gradle

## 소프트웨어 아키텍처

![image](https://github.com/f-lab-edu/fkream-batch/assets/79684851/5822a1c0-2514-4ed3-922b-a7b0fbff48a2)

## 강의 정리
### 내용 요약

- 삭제에 관한 연산
    - 루트 노드의 제거 - 이것이 원소들 중 최댓값
    - 트리 마지막 자리 노드를 임시로 루트 노드의 자리로 옮긴다.
    - 자식 노드들과 값 비교 후 아래로 이동
        -  양쪽 중 더 큰 값과 비교하여 자리를 바꾼다.

- 어디에 적용 될 수 있는가
  - 우선순위 큐
    - Enqueue 할 때 느슨한 정렬을 이루고 있도록 함
    - Dequeue 할 때 최댓값을 빠르게 찾아낼 수 있도록 함
  - 힙 정렬
    - 정렬되지 않은 원소를 아무 순서로나 최대 힙에 삽입
    - 삽입이 끝나면 힙이 빌떄까지 하니씩 삭제
    - 원소들이 삭제된 순서가 정렬된 순서가 됨

### 후기
- 
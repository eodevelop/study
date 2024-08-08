## 강의 정리
### 내용 요약
<img src="./넓이 우선 순회.png" alt="Alt text" style="width:700px; height:450px;">
<br/>

- 넓이 우선 순회
    - 원칙
        - 수준(level)이 낮은 노드를 우선으로 방문
        - 같은 수준의 노드들 사이에서는
            - 부모 노드의 방문 순서에 따라 방문
            - 왼쪽 자식 노드를 오른쪽 자식 노드보다 먼저 방문
        - 재귀적인 방법이 적합한가?
            - 넓이 우선 순회는 재귀적인 방법이 적합하지 않다.
    - 한 노드를 방문했을 떄 나중에 방문할 노드들을 순서대로 기록해 두어야 한다.
        - 큐(queue)를 사용
        ```python
        class BinaryTree:
            def bft(self):
                traversal = []
                queue = []
                if self.root:
                    queue.append(self.root)
                while queue:
                    current = queue.pop(0)
                    traversal.append(current.data)
                    if current.left:
                        queue.append(current.left)
                    if current.right:
                        queue.append(current.right)
                return traversal
        ```

    

### 후기
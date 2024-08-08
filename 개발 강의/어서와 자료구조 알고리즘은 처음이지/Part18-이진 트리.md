## 강의 정리
### 내용 요약
- 이진트리에서는 어떤 연산들이 가능한지
    - size() - 현재 트리에 포함된 노드의 수를 구함
    - depth() - 현재 트리의 깊이를 구함
    - 순회(traversal) - 정해진 순서대로 노드를 탐색해 방문하는 것
- 이진 트리의 구현 - 노드
    - Node
        - Data
        - Left Child
        - Right Child
- 이진 트리의 구현
    - 루트 노드를 가리키는 root 레퍼런스를 가지고 있어야 함
    - Size의 경우 루트 노드를 기준으로 재귀적으로 구할 수 있음
        - 전체 이진 트리의 크기 = 왼쪽 서브트리의 크기 + 오른쪽 서브트리의 크기 + 1
        ```python
        class Node:
            def size(self):
                left_size = self.left.size() if self.left else 0
                right_size = self.right.size() if self.right else 0
                return left_size + right_size + 1
        ```
        ```python
        class BinaryTree:
            def size(self):
                if self.root:
                    return self.root.size()
                else:
                    return 0
        ```
        - 여기서 Node 클래스는 이진 트리의 노드를 나타내는 클래스이고, BinaryTree 클래스는 이진 트리를 나타내는 클래스
    - Depth의 경우 루트 노드를 기준으로 재귀적으로 구할 수 있음
        - 전체 이진 트리의 depth = 왼쪽 서브트리의 depth와 오른쪽 서브트리의 depth 중 큰 값 + 1
        ```python
        class Node:
            def depth(self):
                left_depth = self.left.depth() if self.left else 0
                right_depth = self.right.depth() if self.right else 0
                return max(left_depth, right_depth) + 1
        ```
        ```python
        class BinaryTree:
            def depth(self):
                if self.root:
                    return self.root.depth()
                else:
                    return 0
        ```
- 이진 트리의 순회
    - 깊이 우선 순회
        - 중위 순회 - 왼쪽 자식 -> 자기 자신 -> 오른쪽 자식
        ```python
        def in_order(self):
            traversal = []
            if self.left:
                traversal += self.left.in_order()
            traversal.append(self.data)
            if self.right:
                traversal += self.right.in_order()
            return traversal
        ```
        - 전위 순회 - 자기 자신 -> 왼쪽 자식 -> 오른쪽 자식
        - 후위 순회 - 왼쪽 자식 -> 오른쪽 자식 -> 자기 자신

### 후기
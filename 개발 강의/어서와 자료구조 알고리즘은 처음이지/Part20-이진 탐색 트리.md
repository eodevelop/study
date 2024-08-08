## 강의 정리
### 내용 요약
- 이진 탐색 트리
    - 모든 노드에 대해서
        - 왼쪽 서브트리에 있는 데이터는 모두 현재 노드의 데이터보다 작다.
        - 오른쪽 서브트리에 있는 데이터는 모두 현재 노드의 데이터보다 크다.
    - 중복 되는 데이터는 없다고 가정하고 진행한다.
    - 배열을 이용한 이진 탐색과 비슷하다.
        - 장점은 이진 탐색 보다 추가 삭제가 용이하다.
- 이진 탐색 트리의 추상적 자료구조
    - insert(key, data) - 트리에 주어진 데이터 원소를 추가
    ```python
    class Node:
        def insert(self, key, data):
            if key < self.key:
                if self.left:
                    self.left.insert(key, data)
                else:
                    self.left = Node(key, data)
            elif key > self.key:
                if self.right:
                    self.right.insert(key, data)
                else:
                    self.right = Node(key, data)
            else:
                raise KeyError('Key %s already exists.' % key)
    ```
    - remove(key) - 특정 원소를 트리로부터 삭제 -> 복잡하니 다음 강의에서
    - lookup(key) - 특정 원소를 검색
    ```python
    class Node:
        def lookup(self, key, parent=None):
            if key < self.key:
                if self.left:
                    return self.left.lookup(key, self)
                else:
                    return None, None
            elif key > self.key:
                if self.right:
                    return self.right.lookup(key, self)
                else:
                    return None, None
            else:
                return self, parent
    ```
    - inorder() - 키의 순서대로 데이터 원소를 나열
    ```python
    class Node:
        def inorder(self):
            traversal = []
            if self.left:
                traversal += self.left.inorder()
            traversal.append(self)
            if self.right:
                traversal += self.right.inorder()
            return traversal
    ```
    - min(), max() - 트리에서 가장 작은 원소와 가장 큰 원소를 각각 탐색
    ```python
    class Node:
        def min(self):
            if self.left:
                return self.left.min()
            else:
                return self
        def max(self):
            if self.right:
                return self.right.max()
            else:
                return self
    ```

### 후기
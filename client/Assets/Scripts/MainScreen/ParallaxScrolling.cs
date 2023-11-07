using UnityEngine;

public class ParallaxScrolling : MonoBehaviour
{
    public float scrollSpeed = 10.0f; // 스크롤 속도
    public float cycleLength = 352.34f; // 한 사이클의 길이
    private Vector3 startPosition;

    private void Start()
    {
        startPosition = transform.position; // 초기 위치 저장
    }

    private void Update()
    {
        // x 축 방향으로 이동
        float newPosition = Mathf.Repeat(Time.time * scrollSpeed, cycleLength);
        transform.position = startPosition + Vector3.left * newPosition;
    }
}

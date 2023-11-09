using System.Collections;
using System.Collections.Generic;
using Unity.VisualScripting;
using UnityEngine;
using UnityEngine.SceneManagement;
using TMPro;
public class DayTimer : MonoBehaviour
{
    private TextMeshProUGUI counter;
    private float time;
    // Start is called before the first frame update
    
    void Start()
    {
        time = 0.0f;
        counter = GetComponent<TextMeshProUGUI>();
        StartCoroutine(ExecuteAfterTime(10f));
    }
    
    IEnumerator ExecuteAfterTime(float time)
    {
        yield return new WaitForSeconds(time);
        // 다른 스크립트에서 WebSocketClientManager의 인스턴스에 접근
        WebSocketClientManager webSocketManager = FindObjectOfType<WebSocketClientManager>();

        // WebSocketClientManager의 인스턴스가 존재하는지 확인
        if (webSocketManager != null) {
            // SendRoundEnd() 함수 호출
            webSocketManager.SendRoundEnd();
        } else {
            Debug.LogError("WebSocketClientManager not found in the scene.");
        }

        SceneManager.LoadScene("Night");
    }


    // Update is called once per frame
    void Update()
    {
        time += Time.deltaTime;
        int intTime = 60 - (int)time;
        counter.text = intTime.ToString();
    }
}

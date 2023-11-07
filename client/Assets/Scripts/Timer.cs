using System.Collections;
using System.Collections.Generic;
using Unity.VisualScripting;
using UnityEngine;
using UnityEngine.SceneManagement;

public class Timer : MonoBehaviour
{
    private float time;
    // Start is called before the first frame update
    
    void Start()
    {
        time = 0.0f;
        StartCoroutine(ExecuteAfterTime(15.0f));
    }

    // Update is called once per frame
    void Update()
    {
        time += Time.deltaTime;
    }
    
    IEnumerator ExecuteAfterTime(float time)
    {
        yield return new WaitForSeconds(time);
        
        int roomNo = PlayerPrefs.GetInt("RoomNo");
        Debug.Log(roomNo);
        roomNo %= 3;
        roomNo += 1;
        PlayerPrefs.SetInt("RoomNo",roomNo);
        string room = PlayerPrefs.GetString("Room"+roomNo.ToString());
        Debug.Log("여기부터 : "+roomNo);
        Debug.Log("여기부터 : "+room);
        
        SceneManager.LoadScene(room);
    }
}

using System.Collections;
using System.Collections.Generic;
using Unity.VisualScripting;
using UnityEngine;
using UnityEngine.SceneManagement;

public class Timer : MonoBehaviour
{
    string[] roomNames = new string[] { "", "BiRyoRoom", "medicineroom", "controlRoom" };
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
        
        int roomNo = VariableManager.Instance.roomNo;
        string room = roomNames[roomNo];
        Debug.Log("여기부터 : "+roomNo);
        Debug.Log("여기부터 : "+room);
        
        SceneManager.LoadScene(room);
    }
}

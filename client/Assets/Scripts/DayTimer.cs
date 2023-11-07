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
        StartCoroutine(ExecuteAfterTime(5f));
    }
    
    IEnumerator ExecuteAfterTime(float time)
    {
        yield return new WaitForSeconds(time);
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

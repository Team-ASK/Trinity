using UnityEngine;
using TMPro;
using System.Collections;
public class TextTransparency : MonoBehaviour
{
    TextMeshProUGUI textMeshPro; // TextMeshPro 오브젝트를 연결할 public 변수
    private float time;
    public string Text;
    Color newColor;
    void Start()
    {
        // textMeshPro 변수에 TextMeshProUGUI 컴포넌트를 가져옴
        textMeshPro = gameObject.GetComponent<TextMeshProUGUI>();
        time = 0.0f;
        newColor = textMeshPro.color;
        StartCoroutine(ExecuteAfterTime(6.5f));
        newColor.a = 0.0f;
        textMeshPro.color = newColor;
    }
    IEnumerator ExecuteAfterTime(float time)
    {
        yield return new WaitForSeconds(time);
        textMeshPro.text = Text; 
    }

    void Update()
    {
        time += Time.deltaTime;
        if (time < 2){
            newColor.a += Time.deltaTime * 0.5f;
        }
        if (time > 4 && time < 6){
            newColor.a -= Time.deltaTime * 0.5f;
        }
        
        if (time > 7 && time < 9){
            newColor.a += Time.deltaTime * 0.5f;        
        }
        if (time > 11 && time < 13){
            newColor.a -= Time.deltaTime * 0.5f;
        }
        

        textMeshPro.color = newColor; // 변경된 투명도를 적용
    }
}
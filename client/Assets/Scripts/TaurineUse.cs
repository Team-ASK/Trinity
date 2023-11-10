using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

public class TaurineUse : MonoBehaviour, IPointerClickHandler
{
    GameObject dividLine;
    void Start()
    {
        if(VariableManager.Instance.hasTaurine != 1){
            gameObject.SetActive(false);
        }
        dividLine = GameObject.Find("DividLine");
    }
    public void OnPointerClick(PointerEventData eventData){
        VariableManager.Instance.usedTaurine = true;
        VariableManager.Instance.workLimit = 0;
        VariableManager.Instance.hasTaurine = 0;
        dividLine.GetComponent<Image>().color = Color.white;
        gameObject.SetActive(false);
    }




    // Start is called before the first frame update

    // Update is called once per frame
    void Update()
    {
        
    }
}

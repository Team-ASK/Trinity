using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
public class Shake : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {
        
        gameObject.transform.DOShakePosition(0.1f,0.1f,1000,0,false).SetLoops(-1,LoopType.Restart);
    }

    // Update is called once per frame
    void Update()
    {
    }
}

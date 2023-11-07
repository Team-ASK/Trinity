using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
public class rotate1 : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {
        transform.DORotate(new Vector3(0,0,540*200),12f*200,RotateMode.FastBeyond360)
                     .SetEase(Ease.Linear)
                     .SetLoops(-1);
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}

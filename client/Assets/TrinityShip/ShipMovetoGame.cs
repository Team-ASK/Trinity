using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
public class ShipMovetoGame : MonoBehaviour
{
    
    public Vector3 Pos;
    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
    }

    public void StartMove(){
        transform.DOMove(Pos, 2);
    }
}

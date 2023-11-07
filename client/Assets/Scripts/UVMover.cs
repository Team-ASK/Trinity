using UnityEngine;
using UnityEngine.UI;

public class UVMover : MonoBehaviour
{
    RawImage rawImage;
    Rect uvRect;

    public float speed = 0.08f;

    void Start()
    {
        rawImage = GetComponent<RawImage>();
    }

    void Update()
    {
        uvRect = rawImage.uvRect;
        uvRect.x += Time.deltaTime * speed;
        uvRect.y += Time.deltaTime * 0.5f * speed;
        rawImage.uvRect = uvRect;
    }
}
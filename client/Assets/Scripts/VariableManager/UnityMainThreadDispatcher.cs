using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class UnityMainThreadDispatcher : MonoBehaviour
{
    private static UnityMainThreadDispatcher instance = null;
    private Queue<System.Action> actionQueue = new Queue<System.Action>();

    void Awake()
    {
        if (instance == null)
        {
            instance = this;
        }
    }

    public static void Enqueue(System.Action action)
    {
        if (instance != null)
        {
            instance.actionQueue.Enqueue(action);
        }
    }

    void Update()
    {
        while (actionQueue.Count > 0)
        {
            actionQueue.Dequeue().Invoke();
        }
    }
}

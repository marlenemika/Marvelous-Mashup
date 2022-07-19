using System;
using UnityEngine;

namespace Settings {
  [CreateAssetMenu(fileName = "SettingsObject", menuName = "ScriptableObjects/SettingsObject", order = 1)]
  public class SettingsObject : ScriptableObject
  {
      public string username = "Player";
      public bool isVolumeOn = true;
      public float volumeLevel = 0.5f;

      public void Awake()
      {

      }

      public void OnEnable()
      {

      }

      public void OnDestroy()
      {

      }

      public void UpdateLocalFile()
      {

      }
  }
}

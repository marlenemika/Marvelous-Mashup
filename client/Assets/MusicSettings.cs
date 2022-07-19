using Settings;
using UnityEngine;
using UnityEngine.UI;

public class MusicSettings : MonoBehaviour
{
    public AudioSource audioSource;
    public SettingsObject settingsObject;
    public Slider VolumeSlider;

    private void Awake()
    {
        GameObject [] objs = GameObject.FindGameObjectsWithTag("Music");
        if (objs.Length > 1)
            Destroy(this.gameObject);

        DontDestroyOnLoad(this.gameObject);
    }
     
    public void Start()
    {
        audioSource = GetComponent<AudioSource>();
    }
    
    //sound activation
    public void SetMusic()
    {
        if (settingsObject.isVolumeOn)
        {
            audioSource.Play();
        }
        else
        {
            Debug.Log("hier");
            audioSource.Pause();
        }
    }
    
    //Called when Slider is moved
    public void changeVolume(float sliderValue)
    {
        audioSource.volume = sliderValue;
    }

    //Register Slider Events
    public void OnEnable()
    {
        VolumeSlider.onValueChanged.AddListener(delegate {changeVolume(VolumeSlider.value); });
    }

    //Unregister Slider Events
    public void OnDisable()
    {
        VolumeSlider.onValueChanged.RemoveAllListeners();
    }
}
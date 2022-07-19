#if UNITY_STANDALONE_WIN
using System;
using System.IO;
using AnotherFileBrowser.Windows;

/// <summary>
/// A singleton class for importing files and accessing the systems file browser.
/// </summary>
public sealed class ConfigLoader
{
    private ConfigLoader()
    {}

    /// <summary>
    /// Import a configuration file by prompting the user to select a file. If selected the configuration type is
    /// recognized by the files name and the content is validated.
    /// </summary>
    /// <returns>If the file is valid, it returns a tuple containing the file type and its content, else null</returns>
    public Tuple<ConfigType, string> LoadConfigurationFile()
    {
        // open file browser
        var path = OpenSingleFileSelectorPanel(
            "Load an existing configuration file", 
            "C:\\Users", 
            "JSON files (*.json) | *.json;"
        );
        
        // check if a file was selected
        if (path == null) return null;
        
        // get FileInfo object from selected file
        var file = new FileInfo(path);
        
        // read content of selected file
        var content = file.OpenText().ReadToEnd();
        
        // case -> file is character config
        if (file.Name.EndsWith(".character.json"))
            // validate format and return null or tuple
            return !ConfigValidator.Instance.ValidateFile(ConfigType.CharacterConfig, content) ? 
                null : new Tuple<ConfigType, string>(ConfigType.CharacterConfig, content);

        // case -> file is scenario config
        if (file.Name.EndsWith(".scenario.json"))
            // validate format and return null or tuple
            return !ConfigValidator.Instance.ValidateFile(ConfigType.ScenarioConfig, content) ? 
                null : new Tuple<ConfigType, string>(ConfigType.ScenarioConfig, content);

        // case -> file is match config
        if (file.Name.EndsWith(".game.json"))
            // validate format and return null or tuple
            return !ConfigValidator.Instance.ValidateFile(ConfigType.MatchConfig, content) ? 
                null : new Tuple<ConfigType, string>(ConfigType.MatchConfig, content);

        return null;
    }

    /// <summary>
    /// Open a file browser and prompt the user to select a file.
    /// </summary>
    /// <param name="title">Title of the file browser window</param>
    /// <param name="directory">Starting directory</param>
    /// <param name="filter">File filter of type "name_of_the_filter | *.extension; (optional more extensions seperated by semicolons )" </param>
    /// <returns>The path to the selected file</returns>
    public string OpenSingleFileSelectorPanel(string title, string directory, string filter)
    { 
        // create window properties
        var bp = new BrowserProperties {title = title, initialDir = directory, filter = filter, filterIndex = 0};

        // declare path variable
        string path = null;
        new FileBrowser().OpenFileBrowser(bp, resultValue =>
        {
            // set path to selected directory
            path = resultValue;
        });
        
        return path;
    }
    
    /// <summary>
    /// Open a directory browser and prompt the user to select a directory.
    /// </summary>
    /// <param name="title">Title of the file browser window</param>
    /// <param name="directory">Starting directory</param>
    /// <returns>The path to the selected directory</returns>
    public string OpenSingleDirectorySelectorPanel(string title, string directory)
    { 
        // create window properties
        var bp = new BrowserProperties {title = title, initialDir = directory, filterIndex = 0};

        // declare path variable
        string path = null;
        new FileBrowser().OpenFolderBrowser(bp, resultValue =>
        {
            // set path to selected directory
            path = resultValue;
        });
        
        return path;
    }

    public static ConfigLoader Instance => Lazy.Value;

    private static readonly Lazy<ConfigLoader> Lazy = new Lazy<ConfigLoader>(() => new ConfigLoader());
}
#endif
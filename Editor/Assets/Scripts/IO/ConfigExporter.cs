#if UNITY_STANDALONE_WIN
using System;
using System.IO;
using System.Text;

public sealed class ConfigExporter
{
    private ConfigExporter()
    {}

    /// <summary>
    /// Export a configuration file to a directory, selected by the user.
    /// </summary>
    /// <param name="configType">Type of configuration passed -> used to determine file extension</param>
    /// <param name="json">JSON value to be written to the file</param>
    /// <returns>Whether the export was successful or not</returns>
    public bool ExportConfigurationFile(ConfigType configType, string json)
    {
        // prompt user to select an export directory
        var path = ConfigLoader.Instance.OpenSingleDirectorySelectorPanel("Select a export location.", "");
        
        // get file extension base on config type
        string extension = configType switch
        {
            ConfigType.CharacterConfig => ".character.json",
            ConfigType.ScenarioConfig => ".scenario.json",
            ConfigType.MatchConfig => ".game.json",
            _ => null
        };

        // get FileInfo to not overwrite existing files
        var file = new FileInfo(path + "\\configuration" + extension);

        // if file name is unused export string // else try several endings
        if (!file.Exists)
        {
            file.Create().Write(Encoding.ASCII.GetBytes(json), 0, json.Length);
            return true;
        }
        else
            // increase file version number
            for (var i = 1; i < 999; i++)
            {
                // get file with current number
                file = new FileInfo(path + "\\configuration(" + i + ")" + extension);
                
                // try next number if file exists
                if (file.Exists) continue;
            
                // create file and open stream
                var stream = file.Create();
                
                // write to file using file stream
                stream.Write(Encoding.ASCII.GetBytes(json), 0, json.Length);

                // close file stream
                stream.Close();
                
                // return true to confirm export
                return true;
            }

        // reached if 1000 files in the directory -> export unsuccessful
        return false;
    }

    public static ConfigExporter Instance => Lazy.Value;

    private static readonly Lazy<ConfigExporter> Lazy = new Lazy<ConfigExporter>(() => new ConfigExporter());
}
#endif
using System;
using UnityEngine;
using UnityEngine.UI;
using Random = UnityEngine.Random;

public class ScenarioConfigController : MonoBehaviour
{
    // architecture requirements
    public ConfigRepository configRepository;
    public ScenarioConfig scenarioConfig;

    // constant array values
    public const string GRASS = "GRASS";
    public const string ROCK = "ROCK";
    public const string PORTAL = "PORTAL";

    // UI components
    public Slider widthSlider, heightSlider;
    public Text widthText, heightText;
    public InputField nameInputField, authorInputField;
    
    public float xSpace, ySpace;
    
    public GameObject grassTile, rockTile, portalTile;
    public int leftMargin, rightMargin, topMargin, bottomMargin;
    private int _colSize, _rowSize;
    
    private GameObject[,] _tiles;
    private Vector2 _scenarioEditorSize = new Vector2(Screen.width, Screen.height);
    private float _tileSize;

    // set to true forces UI to rerender all components with current values
    private bool loadScenarioConfigFlag = false;

    private void Start()
    {
        nameInputField.text = scenarioConfig.name;
        authorInputField.text = scenarioConfig.author;
        _rowSize = (int)widthSlider.value;
        _colSize = (int)heightSlider.value;
        _tiles = new GameObject[_rowSize, _colSize];

        RenderGrid();
    }

    /// <summary>
    /// Lifecycle method, called on every frame
    ///
    /// contains:
    ///     - tile interaction
    /// </summary>
    private void Update()
    {
        var mouseX = Input.mousePosition.x;
        var mouseY = Input.mousePosition.y;

        var currentScreenSize = new Vector2(Screen.width, Screen.height);
        if (currentScreenSize != _scenarioEditorSize)
        {
            _scenarioEditorSize = currentScreenSize;
            RenderGrid();
        }
        
        if (Input.GetMouseButtonDown(0))
        {
            // // If the mouse is on top of the level
            if (mouseX >= leftMargin && mouseX <= leftMargin + _rowSize * (_tileSize + xSpace)
                && mouseY >= _scenarioEditorSize.y - topMargin - _colSize * (_tileSize + ySpace)
                && mouseY <= _scenarioEditorSize.y - topMargin)
            {

                // Calculate position (x, y) of the nearest tile to the mouse
                // save nearest tile position in minX and minY
                double minDist = Mathf.Sqrt((mouseX - leftMargin) * (mouseX - leftMargin) + (_scenarioEditorSize.y - mouseY - topMargin) * (_scenarioEditorSize.y - mouseY - topMargin));
                var minX = 0;
                var minY = 0;
                for (var x = 0; x < _rowSize; x++)
                {
                    for (var y = 0; y < _colSize; y++)
                    {
                        var xDist = mouseX - (leftMargin + x * (_tileSize + xSpace) + _tileSize / 2);
                        var yDist = mouseY - (_scenarioEditorSize.y - topMargin - y * (_tileSize + ySpace) - _tileSize / 2);
                        double dist = Mathf.Sqrt(xDist * xDist + yDist * yDist);

                        if (!(dist < minDist)) continue;

                        minDist = dist;
                        minX = x;
                        minY = y;
                    }
                }

                // change clicked tile
                if (scenarioConfig.scenario[minX, minY].Equals(GRASS))
                {
                    scenarioConfig.scenario[minX, minY] = ROCK;
                }
                else if (scenarioConfig.scenario[minX, minY].Equals(ROCK))
                {
                    scenarioConfig.scenario[minX, minY] = PORTAL;
                }
                else
                {
                    scenarioConfig.scenario[minX, minY] = GRASS;
                }
                // render changed tile
                UpdateOnTileChanged(minX, minY);
            }
        }
    }

    /// <summary>
    /// Extended configuration reload, also reloading grid sizes depending on new data layer values.
    /// Call after file import or other data layer changes whithout using the UI inputs.
    /// </summary>
    private void loadScenarioConfig()
    {
        loadScenarioConfigFlag = true;
        DestroyAllTiles();

        _rowSize = scenarioConfig.scenario.GetLength(0);
        _colSize = scenarioConfig.scenario.GetLength(1);
        _tiles = new GameObject[_rowSize, _colSize];
        widthSlider.value = _rowSize;
        heightSlider.value = _colSize;
        widthText.text = "Width: " + _rowSize + " tiles";
        heightText.text = "Height: " + _colSize + " tiles";
        nameInputField.text = scenarioConfig.name;
        authorInputField.text = scenarioConfig.author;

        RenderGrid();
        loadScenarioConfigFlag = false;
    }

    /// <summary>
    /// Handles new name value in UI. Updating data layer if valid value.
    /// </summary>
    public void OnAuthorChanged()
    {
        var newValue = authorInputField.text;
        scenarioConfig.author = newValue;
    }
    
    /// <summary>
    /// Handles new name value in UI. Updating data layer if valid value.
    /// </summary>
    public void OnNameChanged()
    {
        var newValue = nameInputField.text;

        if (newValue != String.Empty)
            scenarioConfig.name = newValue;
        else
            nameInputField.text = scenarioConfig.name;
    }

    /// <summary>
    /// Randomizing all scenario array fields
    ///
    /// Grass = 85%
    /// Rock = 15%
    /// </summary>
    private void RandomiseTiles()
    {
        scenarioConfig.scenario = new string[_rowSize, _colSize];

        for (var x = 0; x < _rowSize; x++)
        {
            for (var y = 0; y < _colSize; y++)
            {
                if (Random.value < 0.85f)
                {
                    scenarioConfig.scenario[x, y] = GRASS;
                }
                else
                {
                    scenarioConfig.scenario[x, y] = ROCK;
                }
            }
        }
    }

    /// <summary>
    /// Generate tiles on screen.
    /// </summary>
    private void RenderGrid()
    {
        // clear UI and instantiate new tile array with current size
        DestroyAllTiles();
        _tiles = new GameObject[_rowSize, _colSize];

        // calculate tilesize according to available space (margins) and amount of tiles
        _tileSize = Mathf.Min((_scenarioEditorSize.x - leftMargin - rightMargin - (_rowSize * xSpace)) / _rowSize,
                              (_scenarioEditorSize.y - topMargin - bottomMargin - (_colSize * ySpace)) / _colSize);
        // resize tile prefabs
        Vector3 localScale = Vector3.one * (_tileSize / 16);
        grassTile.transform.localScale = localScale;
        rockTile.transform.localScale = localScale;
        portalTile.transform.localScale = localScale;

        // Draw each tile on screen
        for (var x = 0; x < _rowSize; x++)
        {
            for (var y = 0; y < _colSize; y++)
            {
                // Create new Tile
                _tiles[x, y] = Instantiate(GetTileFromId(scenarioConfig.scenario[x, y]), // Select tile
                                           new Vector2(leftMargin + xSpace * x + _tileSize * x, (_scenarioEditorSize.y - topMargin) - ySpace * y - _tileSize * y), // calculate position
                                           Quaternion.identity, // rotation 
                                           GameObject.Find("GridManager").transform // set parent (otherwise it will not be drawn) 
                                          );
            }
        }
    }

    /// <summary>
    /// Destroy all tiles on screen.
    /// </summary>
    private void DestroyAllTiles()
    {
        foreach (GameObject tile in _tiles)
        {
            if (tile != null) Destroy(tile);
        }
    }

    /// <summary>
    /// Replaces the single tile on the passed coordinates.
    /// </summary>
    /// <param name="x">x-coordinate of the tile to regenerate</param>
    /// <param name="y">y-coordinate of the tile to regenerate</param>
    private void UpdateOnTileChanged(int x, int y)
    {
        // Destroy old tile
        if (_tiles[x, y] != null) Destroy(_tiles[x, y]);
        // Draw new tile
        // Prefab was already resized in "RenderGrid()". This Function will never be called after a resize
        _tiles[x, y] = Instantiate(GetTileFromId(scenarioConfig.scenario[x, y]), // Select tile
                                   new Vector2(leftMargin + xSpace * x + _tileSize * x, (_scenarioEditorSize.y - topMargin) - ySpace * y - _tileSize * y), // calculate position
                                   Quaternion.identity, // rotation 
                                   GameObject.Find("GridManager").transform // set parent (otherwise it will not be drawn) 
                                  );
    }

    private GameObject GetTileFromId(string id)
    {
        return id switch
        {
            GRASS => grassTile,
            ROCK => rockTile,
            PORTAL => portalTile,
            _ => grassTile
        };
    }

    /// <summary>
    /// Update data layer array when size is changed via sliders.
    /// New tiles are considered "GRASS" by default.
    /// </summary>
    /// <param name="oldWidth">Width before resizing</param>
    /// <param name="newWidth">Value of new grid width</param>
    /// <param name="oldHeight">Height before resizing</param>
    /// <param name="newHeight">Value of new grid heigth</param>
    private void UpdateOnSizeChanged(int oldWidth, int newWidth, int oldHeight, int newHeight)
    {
        var scenarioTmp = new string[newWidth, newHeight];
        for (var x = 0; x < newWidth; x++)
        {
            for (var y = 0; y < newHeight; y++)
            {
                if (x < oldWidth && y < oldHeight)
                {
                    scenarioTmp[x, y] = scenarioConfig.scenario[x, y];
                }
                else
                {
                    scenarioTmp[x, y] = GRASS;
                }
            }
        }
        scenarioConfig.scenario = scenarioTmp;
    }

    /// <summary>
    /// Update the grids width by changing the slider.
    /// </summary>
    /// <param name="newValue">Slider value after change</param>
    public void OnWidthSliderValueChanged(float newValue)
    {
        if (loadScenarioConfigFlag) return;
        
        UpdateOnSizeChanged(_rowSize, (int)newValue, _colSize, _colSize);
        _rowSize = (int)newValue;
        widthText.text = "Width: " + _rowSize + " tiles";
        
        RenderGrid();
    }

    /// <summary>
    /// Update the grids height by changing the slider.
    /// </summary>
    /// <param name="newValue">Slider value after change</param>
    public void OnHeightSliderValueChanged(float newValue)
    {
        if (loadScenarioConfigFlag) return;
        
        UpdateOnSizeChanged(_rowSize, _rowSize, _colSize, (int)newValue);
        _colSize = (int)newValue;
        heightText.text = "Height: " + _colSize + " tiles";
        
        RenderGrid();
    }

    /// <summary>
    /// Randomize all fields in the array and redraw UI
    /// </summary>
    public void OnRandomiseButtonPressed()
    {
        RandomiseTiles();
        RenderGrid();
    }

    /// <summary>
    /// Start object import.
    /// </summary>
    public void OnLoadPressed()
    {
        var success = configRepository.LoadConfigurationFile();

        if (!success)
        {
            DialogManager.PopUpDialog("Import canceled",
                "The import wasn't successful, either because no file was selected or the selected file's format was invalid.");
            return;
        }

        loadScenarioConfig();
    }

    /// <summary>
    /// Start object export.
    /// </summary>
    public void OnSavePressed()
    {
        // count grass tiles
        var grassCounter = 0;
        for (var x = 0; x < _rowSize; x++)
        {
            for (var y = 0; y < _colSize; y++)
            {
                if (scenarioConfig.scenario[x, y].Equals(GRASS)) grassCounter++;
            }
        }

        // ensure the scenario has at least 20 fields of grass
        if (grassCounter < 20)
        {
            DialogManager.PopUpDialog("Not enough grass",
                "The game board has to contain at least 20 fields of grass.");
            return;
        }

        if (scenarioConfig.name == "")
        {
            DialogManager.PopUpDialog("Level name missing",
                "Please enter a level name.");
            return;
        }
        
        // start export process
        var success = configRepository.ExportFile(ConfigType.ScenarioConfig);

        if (!success)
        {
            DialogManager.PopUpDialog("Export canceled",
                "The export wasn't successful, try it again and select another directory");
        }
    }
}

using System;
using Ingame.Tiles;
using Network.Objects;
using Objects;
using UnityEngine;

namespace Ingame
{
    public class GridManager : MonoBehaviour
    {
        [SerializeField] private GrassTile topLeftGrassTile;
        [SerializeField] private GrassTile topMidGrassTile;
        [SerializeField] private GrassTile topRightGrassTile;
        [SerializeField] private GrassTile midLefGrassTile;
        [SerializeField] private GrassTile midMidGrassTile;
        [SerializeField] private GrassTile midRightGrassTile;
        [SerializeField] private GrassTile bottomLeftGrassTile;
        [SerializeField] private GrassTile bottomMidGrassTile;
        [SerializeField] private GrassTile bottomRightGrassTile;
        [SerializeField] private RockTile _rock;
        [SerializeField] private PortalTile _portal;
        [SerializeField] private InfinityStoneTile _infinityStone;
        [SerializeField] private CharacterTile _characterTile;
        [SerializeField] private NPCTile _thanosTile;
        [SerializeField] private NPCTile _stanLee;
        [SerializeField] private NPCTile _goose;
        [SerializeField] private Transform _camera;

        public GameState gameState;
        private int _colSize, _rowSize;
        public GridObject[,,] _entities; // x, y, z (stacked GameObjects)
        public GridObject[,] _characters;

        private void Start()
        {
            _camera.transform.position = new Vector3((float) _rowSize / 2 - 0.5f, (float) _colSize / 2 - 0.5f, -10);
        }
        
        /// <summary>
        /// Updates all information according to new GameState when called
        /// and returns the active character
        /// </summary>
        public void OnGameStateChanged()
        {
            _rowSize = gameState.mapSize[0];
            _colSize = gameState.mapSize[1];
            
            if (_entities != null) DestroyAllEntities();
            _entities = new GridObject[_rowSize, _colSize, 2];
            _characters = new GridObject[_rowSize, _rowSize];

            RenderGrid();
            RenderCharacters();
        }

        /// <summary>
        /// Generate tiles on screen.
        /// </summary>
        private void RenderGrid()
        {
            // Draw each grass tile on screen
            for (var x = 0; x < _rowSize; x++)
            {
                for (var y = 0; y < _colSize; y++)
                {
                    // Create new Tile
                    _entities[x, y, 0] = SpawnGrassTile(x, y);
                }
            }

            try
            {
                lock (gameState)
                    // Draw each Rocks and Portals on screen
                    foreach (var entity in gameState.entities)
                    {
                        if (entity.entityType == EventEntityType.Rock)
                        {
                            _entities[entity.position[0], entity.position[1], 1] =
                                SpawnRockTile(entity.position[0], entity.position[1]);
                        }
                        else if (entity.entityType == EventEntityType.Portal)
                        {
                            _entities[entity.position[0], entity.position[1], 1] =
                                SpawnPortalTile(entity.position[0], entity.position[1]);
                        }
                        else if (entity.entityType == EventEntityType.InfinityStone)
                        {
                            _entities[entity.position[0], entity.position[1], 1] =
                                SpawnInfinityStoneTile(entity.position[0], entity.position[1], entity.ID);
                        }
                        else if (entity.entityType == EventEntityType.NPC)
                        {
                            _entities[entity.position[0], entity.position[1], 1] =
                                SpawnNPC(entity.position[0], entity.position[1], entity.ID);
                        }
                    }
            }
            catch (Exception e)
            {
                Debug.LogWarning($"Rock and Portal drawing failed \n {e.Message}");
            }
        }

        /// <summary>
        /// Generates characters on screen
        /// </summary>
        private void RenderCharacters()
        {
            try {
                lock (gameState)
                    // Draw Characters on screen
                    foreach (var entity in gameState.entities)
                    {
                        if (entity.entityType == EventEntityType.Character)
                        {
                            var characterTile = (CharacterTile) SpawnCharacterTile(entity.position[0], entity.position[1]);
                            characterTile.Init(entity.position[0], entity.position[1], entity, gameState.activeCharacter);
                            _characters[entity.position[0], entity.position[1]] = characterTile;
                        }
                    }
            }
            catch (Exception e)
            {
                Debug.LogWarning($"Character drawing failed \n {e.Message}");
                
                // Remove all drawn entities if drawing failed
                var gridManager = GameObject.Find("GridManager").transform;
                foreach (Transform child in gridManager)
                {
                    Destroy(child.gameObject);
                }
            }
        }

        /// <summary>
        /// Destroy all tiles on screen.
        /// </summary>
        private void DestroyAllEntities()
        {
            foreach (var entity in _entities)
            {
                if (entity != null) Destroy(entity.gameObject);
            }

            foreach (var character in _characters)
            {
                if (character != null) Destroy(character.gameObject);
            }
        }

        /// <summary>
        /// Selects and spawns a fitting grass tile for the position on the grid
        /// </summary>
        private GridObject SpawnGrassTile(int x, int y)
        {
            GrassTile spawnedGrassTile;
            // Bottom Row
            if (y == 0)
            {
                // Left
                if (x == 0)
                {
                    spawnedGrassTile = (GrassTile) InstantiateGridObject(x, y, bottomLeftGrassTile);
                }
                // Right
                else if (x == _rowSize - 1)
                {
                    spawnedGrassTile = (GrassTile) InstantiateGridObject(x, y, bottomRightGrassTile);
                }
                // Mid
                else
                {
                    spawnedGrassTile = (GrassTile) InstantiateGridObject(x, y, bottomMidGrassTile);
                }
            }
            // Top Row
            else if (y == _colSize - 1)
            {
                // Left
                if (x == 0)
                {
                    spawnedGrassTile = (GrassTile) InstantiateGridObject(x, y, topLeftGrassTile);
                }
                // Right
                else if (x == _rowSize - 1)
                {
                    spawnedGrassTile = (GrassTile) InstantiateGridObject(x, y, topRightGrassTile);
                }
                // Mid
                else
                {
                    spawnedGrassTile = (GrassTile) InstantiateGridObject(x, y, topMidGrassTile);
                }
            }
            // Mid Row
            else
            {
                // Left
                if (x == 0)
                {
                    spawnedGrassTile = (GrassTile) InstantiateGridObject(x, y, midLefGrassTile);
                }
                // Right
                else if (x == _rowSize - 1)
                {
                    spawnedGrassTile = (GrassTile) InstantiateGridObject(x, y, midRightGrassTile);
                }
                // Mid
                else
                {
                    spawnedGrassTile = (GrassTile) InstantiateGridObject(x, y, midMidGrassTile);
                }
            }

            spawnedGrassTile.Init(x, y);
            spawnedGrassTile.name = "GrassTile " + x + " " + y;
            return spawnedGrassTile;
        }

        /// <summary>
        /// Spawns a rock on the grid
        /// </summary>
        private GridObject SpawnRockTile(int x, int y)
        {
            var spawnedRockTile = (RockTile) InstantiateGridObject(x, y, _rock);
            spawnedRockTile.Init(x, y);
            spawnedRockTile.transform.position += new Vector3(0, 0, 0);
            spawnedRockTile.name = "RockTile " + x + " " + y;
            return spawnedRockTile;
        }

        /// <summary>
        /// Spawns a portal on the grid
        /// </summary>
        private GridObject SpawnPortalTile(int x, int y)
        {
            var spawnedPortalTile = (PortalTile) InstantiateGridObject(x, y, _portal);
            spawnedPortalTile.Init(x, y);
            spawnedPortalTile.transform.position += new Vector3(0, 0, 0);
            spawnedPortalTile.name = "PortalTile " + x + " " + y;
            return spawnedPortalTile;
        }

        /// <summary>
        /// Spawns an Infinity Stone on the grid
        /// </summary>
        private GridObject SpawnInfinityStoneTile(int x, int y, int type)
        {
            var spawnedInfinityStoneTile = (InfinityStoneTile) InstantiateGridObject(x, y, _infinityStone);
            spawnedInfinityStoneTile.Init(x, y, type);
            spawnedInfinityStoneTile.transform.position += new Vector3(0, 0, 0);
            spawnedInfinityStoneTile.name = "Infinity Stone " + x + " " + y;
            return spawnedInfinityStoneTile;
        }

        /// <summary>
        /// Spawns a character on the grid
        /// </summary>
        private GridObject SpawnCharacterTile(int x, int y)
        {
            var spawnedCharacterTile = (CharacterTile) InstantiateGridObject(x, y, _characterTile);
            spawnedCharacterTile.transform.position += new Vector3(0, 0, 0);
            spawnedCharacterTile.name = "CharacterTile " + x + " " + y;
            return spawnedCharacterTile;
        }
        
        private GridObject SpawnNPC(int x, int y, int entityID)
        {
            GridObject spawnedNPCTile = null;
            switch (entityID)
            {
                // Goose
                case 0:
                    spawnedNPCTile = InstantiateGridObject(x, y, _goose);
                    break;
                // Stan Lee
                case 1:
                    spawnedNPCTile = InstantiateGridObject(x, y, _stanLee);
                    break;
                // Thanos
                case 2:
                    spawnedNPCTile = InstantiateGridObject(x, y, _thanosTile);
                    break;
            }
            spawnedNPCTile.name = "NPCTile " + x + " " + y;
            return spawnedNPCTile;
        }

        /// <summary>
        /// Instantiates a gridObject in scene
        /// </summary>
        private static GridObject InstantiateGridObject(int x, int y, GridObject gridObject)
        {
            var spawnedTile = Instantiate(
                gridObject,
                new Vector3(x, y, 0),
                Quaternion.identity,
                GameObject.Find("GridManager").transform
            );
            spawnedTile.transform.localScale = new Vector2(3.05f, 3.05f);
            return spawnedTile;
        }
    }
}

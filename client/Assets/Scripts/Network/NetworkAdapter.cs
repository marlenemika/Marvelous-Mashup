using System;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using NativeWebSocket;
using Network.Events;
using Network.Messages;
using Network.Requests;
using Newtonsoft.Json;
using UnityEngine;

namespace Network
{
    public class NetworkAdapter
    {
        private INetworkListener _owner;

        private string _hostname;
        private int _port;
        private string _username;
        
        private string _role;
        public string Role => _role;
        public bool GameRunning;

        private readonly string _deviceId;

        private WebSocket _webSocket;
        private Thread _listeningThread;

        private NetworkAdapter()
        {
            _deviceId = GetDeviceIdFromIp();
        }

        public void UpdateOwner(INetworkListener owner)
        {
            _owner = owner;
            Debug.Log($"{GetType().Name}: network owner updated to {owner}");
        }

        public async Task<bool> ConnectToServer(string hostname, int port, string username, string role)
        {
            if (_webSocket != null && _webSocket.State != WebSocketState.Closed) return false;

            _hostname = hostname;
            _port = port;
            _username = username;
            _role = role;

            _webSocket = new WebSocket($"ws://{_hostname}:{_port}");

            _webSocket.OnOpen += () =>
            {
                Debug.Log("Connection open!");
                Send(new HelloServer(_username, $"team26-{_deviceId}"));

                _listeningThread = new Thread(Instance.ListenForMessages);
                _listeningThread.Start();
            };

            _webSocket.OnError += (e) =>
            {
                Debug.Log("Error! " + e);
            };

            _webSocket.OnClose += (e) =>
            {
                _listeningThread?.Abort();
                Debug.Log("Connection closed!");
            };

            _webSocket.OnMessage += (bytes) =>
            {
                // getting the message as a string
                var message = Encoding.UTF8.GetString(bytes);
                Debug.Log("Incoming message | " + message);
                
                try
                {
                    var basicEvent = JsonConvert.DeserializeObject<BasicEvent>(message);

                    if (basicEvent == null) return;

                    OnMessageReceived(basicEvent, message);
                    _owner.OnMessageReceived(basicEvent, message);
                }
                catch (Exception)
                {
                    // ignore
                }
                
                try
                {
                    var basicMessage = JsonConvert.DeserializeObject<BasicMessage>(message);

                    if (basicMessage == null) return;

                    OnMessageReceived(basicMessage, message);
                    _owner.OnMessageReceived(basicMessage, message);
                }
                catch (Exception e)
                {
                    Debug.LogWarning(e.Message);
                }
            };

            // waiting for messages
            await _webSocket.Connect();

            return _webSocket.State == WebSocketState.Open;
        }

        private void ListenForMessages()
        {
            #if !UNITY_WEBGL || UNITY_EDITOR
            while (_webSocket.State == WebSocketState.Open)
                _webSocket.DispatchMessageQueue();
            #endif
        }

        private async void OnMessageReceived(BasicMessage message, string json)
        {
            try
            {
                switch (message.messageType)
                {
                    case "HELLO_CLIENT":
                        var helloClient = JsonConvert.DeserializeObject<HelloClient>(json);
                        if (helloClient == null) break;
                        GameRunning = helloClient.runningGame;
                        if (GameRunning && _role.Equals("PLAYER")) await Send(new Reconnect(true));
                        else await Send(new PlayerReady(true, _role));
                        break;
                    case "GOODBYE_CLIENT":
                        await _webSocket.Close();
                        break;
                    case "ERROR":
                        Debug.LogWarning($"Error message received! -> {((Error)message).message}");
                        break;
                }
            }
            catch (Exception)
            {
                //ignore
            }
        }

        private async void OnMessageReceived(BasicEvent basicEvent, string json)
        {
            try
            {
                switch (basicEvent.eventType)
                {
                }
            }
            catch (Exception)
            {
                //ignore
            }
        }

        public async Task<bool> Send(BasicMessage message)
        {
            if (_webSocket.State != WebSocketState.Open) return false;

            var json = JsonConvert.SerializeObject(message);

            var task = _webSocket.SendText(json);
            await task;
            Debug.Log("Sent | " + json);

            return task.IsCompleted;
        }

        public async Task<bool> Send(BasicRequest request)
        {
            if (_webSocket.State != WebSocketState.Open) return false;

            var json = JsonConvert.SerializeObject(request);

            var task = _webSocket.SendText(json);
            await task;
            Debug.Log("Sent | " + json);

            return task.IsCompleted;
        }

        private static string GetDeviceIdFromIp()
        {
            var localIP = "0.0.0.0";
            var host = Dns.GetHostEntry(Dns.GetHostName());
            foreach (var ip in host.AddressList)
            {
                if (ip.AddressFamily != AddressFamily.InterNetwork) continue;

                localIP = ip.ToString();
                break;
            }

            var ipSplit = localIP.Split('.');
            return ipSplit[ipSplit.Length - 2];
        }
        
        public static NetworkAdapter Instance => Lazy.Value;

        private static readonly Lazy<NetworkAdapter> Lazy = new Lazy<NetworkAdapter>(() => new NetworkAdapter());
    }
}

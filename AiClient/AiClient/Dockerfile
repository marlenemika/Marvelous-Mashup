FROM mcr.microsoft.com/dotnet/core/runtime:3.1 AS base
WORKDIR /app

FROM mcr.microsoft.com/dotnet/core/sdk:3.1 AS build
WORKDIR /src
COPY ["AiClient/AiClient.csproj", "AiClient/"]
RUN dotnet restore "AiClient/AiClient.csproj"
COPY . .
WORKDIR "/src/AiClient"
RUN dotnet build "AiClient.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "AiClient.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "AiClient.dll"]

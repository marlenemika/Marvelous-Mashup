
namespace Network.Objects
{
    public class MatchConfig
    {
        public int maxRounds;
        public int maxRoundTime;
        public int maxGameTime;
        public int maxAnimationTime;
        public int spaceStoneCD;
        public int mindStoneCD;
        public int realityStoneCD;
        public int powerStoneCD;
        public int timeStoneCD;
        public int soulStoneCD;
        public int mindStoneDMG;
        public int maxPauseTime;
        public int maxResponseTime;

        public MatchConfig(int maxRounds, int maxRoundTime, int maxGameTime, int maxAnimationTime, int spaceStoneCd,
            int mindStoneCd, int realityStoneCd, int powerStoneCd, int timeStoneCd, int soulStoneCd, int mindStoneDmg,
            int maxPauseTime, int maxResponseTime)
        {
            this.maxRounds = maxRounds;
            this.maxRoundTime = maxRoundTime;
            this.maxGameTime = maxGameTime;
            this.maxAnimationTime = maxAnimationTime;
            spaceStoneCD = spaceStoneCd;
            mindStoneCD = mindStoneCd;
            realityStoneCD = realityStoneCd;
            powerStoneCD = powerStoneCd;
            timeStoneCD = timeStoneCd;
            soulStoneCD = soulStoneCd;
            mindStoneDMG = mindStoneDmg;
            this.maxPauseTime = maxPauseTime;
            this.maxResponseTime = maxResponseTime;
        }
    }
}

namespace AiClient
{
    public class Position
    {
        public int x;
        public int y;

        public Position(int positionX, int positionY)
        {
            x = positionX;
            y = positionY;
        }

        public Position(int[] entityPosition)
        {
            x = entityPosition[0];
            y = entityPosition[1];
        }

        public bool Equals(Position position2)
        {
            return x == position2.x && y == position2.y;
        }

        public Position DeapCopy()
        {
            return new Position(x, y);
        }
    }
}
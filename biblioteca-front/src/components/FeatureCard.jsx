import { Link } from "react-router-dom";
import { Card, CardContent } from "@/components/ui/card";

export default function FeatureCard({
  to,
  icon: Icon,
  title,
  bgColor,
  borderColor,
  iconRotate = 0,
}) {
  return (
    <Link to={to}>
      <Card
        className={`h-28 sm:h-32 ${bgColor} ${borderColor} hover:shadow-lg hover:scale-[1.02] transition-all cursor-pointer`}
      >
        <CardContent className="flex flex-col justify-center items-center h-full p-4">
          <Icon
            className="h-6 w-6 sm:h-8 sm:w-8 text-foreground mb-2"
            style={
              iconRotate !== 0 ? { transform: `rotate(${iconRotate}deg)` } : {}
            }
          />
          <span className="text-sm font-semibold text-foreground text-center leading-tight">
            {title}
          </span>
        </CardContent>
      </Card>
    </Link>
  );
}

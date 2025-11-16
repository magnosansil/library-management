import {
  CircleCheck,
  Info,
  Loader2,
  XOctagon,
  TriangleAlert,
} from "lucide-react";
import { Toaster as Sonner } from "sonner";

const Toaster = ({ ...props }) => {
  return (
    <Sonner
      theme="light"
      className="toaster group"
      icons={{
        success: <CircleCheck className="h-4 w-4" />,
        info: <Info className="h-4 w-4" />,
        warning: <TriangleAlert className="h-4 w-4" />,
        error: <XOctagon className="h-4 w-4" />,
        loading: <Loader2 className="h-4 w-4 animate-spin" />,
      }}
      style={{
        "--normal-bg": "var(--color-popover, hsl(var(--popover)))",
        "--normal-text":
          "var(--color-popover-foreground, hsl(var(--popover-foreground)))",
        "--normal-border": "var(--color-border, hsl(var(--border)))",
        "--border-radius": "var(--radius)",
      }}
      {...props}
    />
  );
};

export { Toaster };

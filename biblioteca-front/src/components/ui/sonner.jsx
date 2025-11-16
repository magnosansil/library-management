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
        "--normal-bg": "white",
        "--normal-text": "var(--color-foreground, oklch(9% 0 0))",
        "--normal-border": "var(--color-border, oklch(90% 0 0))",
        "--border-radius": "var(--radius)",
      }}
      {...props}
    />
  );
};

export { Toaster };

import { useEffect, useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { getAccessToken, setAccessToken } from "@/api/client";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
  Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle,
} from "@/components/ui/dialog";

export function DevTokenDialog({ open, onOpenChange }: { open: boolean; onOpenChange: (open: boolean) => void }) {
  const [token, setToken] = useState("");
  const queryClient = useQueryClient();

  useEffect(() => {
    if (open) setToken(getAccessToken() ?? "");
  }, [open]);

  const save = (value: string | null) => {
    setAccessToken(value);
    void queryClient.invalidateQueries();
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Access token</DialogTitle>
          <DialogDescription>
            Paste the JWT issued by your local backend. It is stored in this browser only and sent as
            an Authorization header with every request.
          </DialogDescription>
        </DialogHeader>
        <div className="space-y-2">
          <Label htmlFor="devtrack-token">JWT</Label>
          <Textarea
            id="devtrack-token"
            value={token}
            onChange={(event) => setToken(event.target.value)}
            placeholder="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            className="min-h-28 font-mono text-xs"
          />
        </div>
        <DialogFooter className="gap-2 sm:justify-between">
          <Button variant="ghost" onClick={() => save(null)}>Clear</Button>
          <Button onClick={() => save(token.trim() || null)}>Save token</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

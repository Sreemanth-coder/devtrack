import { useMemo } from "react";
import { format, parseISO } from "date-fns";
import { EmptyState } from "./data-ui";

export function ContributionHeatmap({ commitsByDate }: { commitsByDate: Record<string, number> }) {
  const entries = useMemo(() => Object.entries(commitsByDate).sort(([a], [b]) => a.localeCompare(b)), [commitsByDate]);
  if (!entries.length) return <EmptyState title="No GitHub activity found for the selected period." />;
  const max = Math.max(...entries.map(([, count]) => count), 1);
  const level = (count: number) => Math.max(1, Math.ceil((count / max) * 4));
  return <div className="overflow-x-auto pb-2"><div className="grid min-w-max grid-flow-col grid-rows-7 gap-1" role="img" aria-label="GitHub contribution activity">{entries.map(([date, count]) => <div key={date} title={`${format(parseISO(date), "MMM d, yyyy")}: ${count} commit${count === 1 ? "" : "s"}`} aria-label={`${date}: ${count} commits`} className={`heat-cell heat-${level(count)}`} />)}</div><div className="mt-3 flex items-center justify-end gap-1 text-xs text-muted-foreground"><span className="mr-1">Less</span>{[0,1,2,3,4].map((item) => <span key={item} className={`heat-cell heat-${item}`} />)}<span className="ml-1">More</span></div></div>;
}

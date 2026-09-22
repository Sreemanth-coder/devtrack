import { createFileRoute } from "@tanstack/react-router";
import { DashboardPage } from "@/components/devtrack/pages";
export const Route = createFileRoute("/dashboard")({
  head: () => ({ meta: [
    { title: "Dashboard — DevTrack" },
    { name: "description", content: "Dashboard insights in the DevTrack Developer Career Command Center." },
    { property: "og:title", content: "Dashboard — DevTrack" },
    { property: "og:description", content: "Dashboard insights in the DevTrack Developer Career Command Center." },
    { property: "og:type", content: "website" },
    { name: "twitter:card", content: "summary_large_image" },
  ]}),
  component: DashboardPage,
});

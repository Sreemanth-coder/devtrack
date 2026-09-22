import { createFileRoute } from "@tanstack/react-router";
import { GithubPage } from "@/components/devtrack/pages";
export const Route = createFileRoute("/github")({
  head: () => ({ meta: [
    { title: "GitHub — DevTrack" },
    { name: "description", content: "GitHub insights in the DevTrack Developer Career Command Center." },
    { property: "og:title", content: "GitHub — DevTrack" },
    { property: "og:description", content: "GitHub insights in the DevTrack Developer Career Command Center." },
    { property: "og:type", content: "website" },
    { name: "twitter:card", content: "summary_large_image" },
  ]}),
  component: GithubPage,
});

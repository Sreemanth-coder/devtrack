import { createFileRoute } from "@tanstack/react-router";
import { ProjectsPage } from "@/components/devtrack/pages";
export const Route = createFileRoute("/projects")({
  head: () => ({ meta: [
    { title: "Projects — DevTrack" },
    { name: "description", content: "Projects insights in the DevTrack Developer Career Command Center." },
    { property: "og:title", content: "Projects — DevTrack" },
    { property: "og:description", content: "Projects insights in the DevTrack Developer Career Command Center." },
    { property: "og:type", content: "website" },
    { name: "twitter:card", content: "summary_large_image" },
  ]}),
  component: ProjectsPage,
});

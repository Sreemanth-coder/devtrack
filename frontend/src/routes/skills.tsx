import { createFileRoute } from "@tanstack/react-router";
import { SkillsPage } from "@/components/devtrack/pages";
export const Route = createFileRoute("/skills")({
  head: () => ({ meta: [
    { title: "Skills — DevTrack" },
    { name: "description", content: "Skills insights in the DevTrack Developer Career Command Center." },
    { property: "og:title", content: "Skills — DevTrack" },
    { property: "og:description", content: "Skills insights in the DevTrack Developer Career Command Center." },
    { property: "og:type", content: "website" },
    { name: "twitter:card", content: "summary_large_image" },
  ]}),
  component: SkillsPage,
});

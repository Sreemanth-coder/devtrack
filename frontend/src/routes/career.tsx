import { createFileRoute } from "@tanstack/react-router";
import { CareerPage } from "@/components/devtrack/pages";
export const Route = createFileRoute("/career")({
  head: () => ({ meta: [
    { title: "Career — DevTrack" },
    { name: "description", content: "Career insights in the DevTrack Developer Career Command Center." },
    { property: "og:title", content: "Career — DevTrack" },
    { property: "og:description", content: "Career insights in the DevTrack Developer Career Command Center." },
    { property: "og:type", content: "website" },
    { name: "twitter:card", content: "summary_large_image" },
  ]}),
  component: CareerPage,
});

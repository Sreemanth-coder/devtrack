import { createFileRoute } from "@tanstack/react-router";
import { DsaPage } from "@/components/devtrack/pages";
export const Route = createFileRoute("/dsa")({
  head: () => ({ meta: [
    { title: "DSA — DevTrack" },
    { name: "description", content: "DSA insights in the DevTrack Developer Career Command Center." },
    { property: "og:title", content: "DSA — DevTrack" },
    { property: "og:description", content: "DSA insights in the DevTrack Developer Career Command Center." },
    { property: "og:type", content: "website" },
    { name: "twitter:card", content: "summary_large_image" },
  ]}),
  component: DsaPage,
});

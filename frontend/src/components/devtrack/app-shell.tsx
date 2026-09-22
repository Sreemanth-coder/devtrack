import { Link, useRouterState } from "@tanstack/react-router";
import { Activity, BarChart3, Blocks, BriefcaseBusiness, Code2, Github, Settings, Sparkles, Menu } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { userQuery } from "@/api/queries";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {
  Sidebar, SidebarContent, SidebarFooter, SidebarGroup, SidebarGroupContent, SidebarGroupLabel,
  SidebarHeader, SidebarInset, SidebarMenu, SidebarMenuButton, SidebarMenuItem, SidebarProvider, SidebarRail, SidebarTrigger, useSidebar,
} from "@/components/ui/sidebar";
import { useState, type ReactNode } from "react";
import { Button } from "@/components/ui/button";
import { DevTokenDialog } from "@/components/devtrack/dev-token-dialog";
import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle } from "@/components/ui/sheet";

const nav = [
  { title: "Dashboard", to: "/dashboard" as const, icon: BarChart3 },
  { title: "DSA", to: "/dsa" as const, icon: Code2 },
  { title: "Skills", to: "/skills" as const, icon: Blocks },
  { title: "Projects", to: "/projects" as const, icon: BriefcaseBusiness },
  { title: "GitHub", to: "/github" as const, icon: Github },
  { title: "Career Analytics", to: "/career" as const, icon: Activity },
];
const titles: Record<string, string> = { "/dashboard": "Dashboard", "/dsa": "DSA Analytics", "/skills": "Skills", "/projects": "Projects", "/github": "GitHub Analytics", "/career": "Career Analytics" };
function Navigation({ onOpenSettings }: { onOpenSettings: () => void }) {
  const path = useRouterState({ select: (state) => state.location.pathname });
  const { state, setOpenMobile } = useSidebar();
  return <Sidebar collapsible="icon" className="border-sidebar-border"><SidebarHeader className="h-16 justify-center border-b border-sidebar-border px-3"><Link to="/dashboard" className="flex items-center gap-3 overflow-hidden"><span className="grid size-8 shrink-0 place-items-center rounded-md bg-primary text-primary-foreground"><Sparkles className="size-4"/></span>{state !== "collapsed" ? <span className="text-sm font-bold tracking-wide">DEVTRACK</span> : null}</Link></SidebarHeader><SidebarContent className="px-2 py-4"><SidebarGroup><SidebarGroupLabel>Command center</SidebarGroupLabel><SidebarGroupContent><SidebarMenu>{nav.map((item) => <SidebarMenuItem key={item.to}><SidebarMenuButton asChild isActive={path === item.to} tooltip={item.title}><Link to={item.to} onClick={() => setOpenMobile(false)}><item.icon/><span>{item.title}</span></Link></SidebarMenuButton></SidebarMenuItem>)}</SidebarMenu></SidebarGroupContent></SidebarGroup></SidebarContent><SidebarFooter className="border-t border-sidebar-border p-2"><Profile/><SidebarMenu><SidebarMenuItem><SidebarMenuButton tooltip="Settings" onClick={onOpenSettings}><Settings/><span>Settings</span></SidebarMenuButton></SidebarMenuItem></SidebarMenu></SidebarFooter><SidebarRail/></Sidebar>;
}
function Profile() {
  const { data } = useQuery(userQuery);
  const { state } = useSidebar();
  const initials = data?.name?.split(" ").map((x) => x[0]).join("").slice(0,2).toUpperCase() || "DT";
  return <div className="flex items-center gap-2 overflow-hidden px-2 py-2"><Avatar className="size-8 shrink-0"><AvatarFallback>{initials}</AvatarFallback></Avatar>{state !== "collapsed" ? <div className="min-w-0"><p className="truncate text-sm font-medium">{data?.name ?? "Developer"}</p><p className="truncate text-xs text-muted-foreground">{data?.githubUsername ? `@${data.githubUsername}` : "DevTrack account"}</p></div> : null}</div>;
}
export function AppShell({ children }: { children: ReactNode }) {
  const path = useRouterState({ select: (state) => state.location.pathname });
  const [mobileOpen, setMobileOpen] = useState(false);
  const [tokenOpen, setTokenOpen] = useState(false);
  return <SidebarProvider><Navigation onOpenSettings={() => setTokenOpen(true)}/><SidebarInset><header className="sticky top-0 z-20 flex h-16 items-center justify-between border-b border-border bg-background/90 px-4 backdrop-blur-md sm:px-6"><div className="flex items-center gap-3"><SidebarTrigger className="hidden size-8 md:inline-flex"/><Button aria-label="Open navigation" variant="ghost" size="icon" className="md:hidden" onClick={() => setMobileOpen(true)}><Menu/></Button><div><p className="text-sm font-semibold text-foreground">{titles[path] ?? "DevTrack"}</p><p className="hidden text-xs text-muted-foreground sm:block">Developer Career Command Center</p></div></div><div className="status-pill"><span className="status-dot"/>Live workspace</div></header><main className="flex-1 px-4 py-6 sm:px-6 lg:px-8">{children}</main></SidebarInset><MobileMenu open={mobileOpen} onOpenChange={setMobileOpen} onOpenSettings={() => setTokenOpen(true)}/><DevTokenDialog open={tokenOpen} onOpenChange={setTokenOpen}/></SidebarProvider>;
}

function MobileMenu({ open, onOpenChange, onOpenSettings }: { open: boolean; onOpenChange: (open: boolean) => void; onOpenSettings: () => void }) {
  const path = useRouterState({ select: (state) => state.location.pathname });
  return <Sheet open={open} onOpenChange={onOpenChange}><SheetContent side="left" className="w-72 border-sidebar-border bg-sidebar p-0"><SheetHeader className="border-b border-sidebar-border px-5 py-5 text-left"><SheetTitle className="flex items-center gap-3"><span className="grid size-8 place-items-center rounded-md bg-primary text-primary-foreground"><Sparkles className="size-4"/></span>DEVTRACK</SheetTitle><SheetDescription>Developer Career Command Center</SheetDescription></SheetHeader><nav className="space-y-1 p-3">{nav.map((item) => <Button key={item.to} variant={path === item.to ? "secondary" : "ghost"} className="w-full justify-start" asChild><Link to={item.to} onClick={() => onOpenChange(false)}><item.icon/><span>{item.title}</span></Link></Button>)}<Button variant="ghost" className="w-full justify-start" onClick={() => { onOpenChange(false); onOpenSettings(); }}><Settings/><span>Settings</span></Button></nav></SheetContent></Sheet>;
}

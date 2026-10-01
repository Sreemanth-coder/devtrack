import { i as __toESM } from "../_runtime.mjs";
import { u as require_react } from "../_libs/@floating-ui/react-dom+[...].mjs";
import { l as Slot, p as require_jsx_runtime } from "../_libs/@radix-ui/react-avatar+[...].mjs";
import { t as queryOptions } from "../_libs/tanstack__react-query.mjs";
import { t as X } from "../_libs/lucide-react.mjs";
import { n as clsx, t as cva } from "../_libs/class-variance-authority+clsx.mjs";
import { t as twMerge } from "../_libs/tailwind-merge.mjs";
import { a as DialogOverlay$1, c as DialogTrigger$1, i as DialogDescription$1, n as DialogClose, o as DialogPortal$1, r as DialogContent$1, s as DialogTitle$1, t as Dialog$1 } from "../_libs/@radix-ui/react-dialog+[...].mjs";
//#region node_modules/.nitro/vite/services/ssr/assets/dialog-Cv4722xo.js
var import_react = /* @__PURE__ */ __toESM(require_react());
var import_jsx_runtime = require_jsx_runtime();
var API_BASE_URL = ({
	"BASE_URL": "/",
	"DEV": false,
	"MODE": "production",
	"PROD": true,
	"SSR": true,
	"TSS_DEV_SERVER": "false",
	"TSS_DEV_SSR_STYLES_BASEPATH": "/",
	"TSS_DEV_SSR_STYLES_ENABLED": "true",
	"TSS_DISABLE_CSRF_MIDDLEWARE_WARNING": "false",
	"TSS_INLINE_CSS_ENABLED": "false",
	"TSS_ROUTER_BASEPATH": "",
	"TSS_SERVER_FN_BASE": "/_serverFn/",
	"VITE_API_BASE_URL": "http://localhost:8080"
}["VITE_API_BASE_URL"] ?? "").replace(/\/$/, "");
var TOKEN_KEY = "devtrack_token";
var ApiError = class extends Error {
	status;
	kind;
	constructor(message, status, kind) {
		super(message);
		this.status = status;
		this.kind = kind;
		this.name = "ApiError";
	}
};
function getAccessToken() {
	return typeof window === "undefined" ? null : window.localStorage.getItem(TOKEN_KEY);
}
function setAccessToken(token) {
	if (typeof window === "undefined") return;
	if (token) window.localStorage.setItem(TOKEN_KEY, token);
	else window.localStorage.removeItem(TOKEN_KEY);
}
async function apiGet(path) {
	const token = getAccessToken();
	if (!API_BASE_URL) throw new ApiError("The DevTrack API address is not configured yet. Set VITE_API_BASE_URL to your backend URL (for example http://localhost:8081) and reload.", null, "network");
	try {
		const response = await fetch(`${API_BASE_URL}${path}`, {
			headers: {
				Accept: "application/json",
				...token ? { Authorization: `Bearer ${token}` } : {}
			},
			credentials: "include"
		});
		if (!response.ok) {
			const kind = response.status === 401 ? "unauthorized" : response.status === 403 ? "forbidden" : "server";
			throw new ApiError(response.status === 401 ? "Your session is missing or has expired." : response.status === 403 ? "You do not have access to this data." : response.status === 429 ? "GitHub is temporarily rate-limiting requests. Please try again shortly." : "The service is temporarily unavailable.", response.status, kind);
		}
		return await response.json();
	} catch (error) {
		if (error instanceof ApiError) throw error;
		throw new ApiError("Unable to reach the DevTrack service.", null, "network");
	}
}
var userQuery = queryOptions({
	queryKey: ["user"],
	queryFn: () => apiGet("/api/users/me"),
	retry: false
});
var dsaQuery = queryOptions({
	queryKey: ["dsa", "stats"],
	queryFn: () => apiGet("/api/dsa/stats"),
	retry: 1
});
var skillsQuery = queryOptions({
	queryKey: ["skills"],
	queryFn: () => apiGet("/api/skills"),
	retry: 1
});
var projectsQuery = queryOptions({
	queryKey: ["projects"],
	queryFn: () => apiGet("/api/projects"),
	retry: 1
});
var githubAnalyticsQuery = queryOptions({
	queryKey: ["github", "analytics"],
	queryFn: () => apiGet("/api/github/analytics"),
	retry: 1
});
var githubActivityQuery = queryOptions({
	queryKey: ["github", "activity"],
	queryFn: () => apiGet("/api/github/activity"),
	retry: 1
});
var skillEvidenceQuery = queryOptions({
	queryKey: ["github", "skill-evidence"],
	queryFn: () => apiGet("/api/github/skill-evidence"),
	retry: 1
});
var leetcodeQuery = queryOptions({
	queryKey: ["leetcode", "stats"],
	queryFn: () => apiGet("/api/leetcode/stats"),
	retry: 1
});
var careerQuery = queryOptions({
	queryKey: ["career", "analytics"],
	queryFn: () => apiGet("/api/career/analytics"),
	retry: 1
});
function cn(...inputs) {
	return twMerge(clsx(inputs));
}
var buttonVariants = cva("inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium cursor-pointer transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50 disabled:cursor-not-allowed [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0", {
	variants: {
		variant: {
			default: "bg-primary text-primary-foreground shadow hover:bg-primary/90",
			destructive: "bg-destructive text-destructive-foreground shadow-sm hover:bg-destructive/90",
			outline: "border border-input bg-background shadow-sm hover:bg-accent hover:text-accent-foreground",
			secondary: "bg-secondary text-secondary-foreground shadow-sm hover:bg-secondary/80",
			ghost: "hover:bg-accent hover:text-accent-foreground",
			link: "text-primary underline-offset-4 hover:underline"
		},
		size: {
			default: "h-9 px-4 py-2",
			sm: "h-8 rounded-md px-3 text-xs",
			lg: "h-10 rounded-md px-8",
			icon: "h-9 w-9"
		}
	},
	defaultVariants: {
		variant: "default",
		size: "default"
	}
});
var Button = import_react.forwardRef(({ className, variant, size, asChild = false, ...props }, ref) => {
	return /* @__PURE__ */ (0, import_jsx_runtime.jsx)(asChild ? Slot : "button", {
		className: cn(buttonVariants({
			variant,
			size,
			className
		})),
		ref,
		...props
	});
});
Button.displayName = "Button";
function Skeleton({ className, ...props }) {
	return /* @__PURE__ */ (0, import_jsx_runtime.jsx)("div", {
		className: cn("animate-pulse rounded-md bg-primary/10", className),
		...props
	});
}
var Dialog = Dialog$1;
var DialogTrigger = DialogTrigger$1;
var DialogPortal = DialogPortal$1;
var DialogOverlay = import_react.forwardRef(({ className, ...props }, ref) => /* @__PURE__ */ (0, import_jsx_runtime.jsx)(DialogOverlay$1, {
	ref,
	className: cn("fixed inset-0 z-50 bg-black/80  data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0", className),
	...props
}));
DialogOverlay.displayName = DialogOverlay$1.displayName;
var DialogContent = import_react.forwardRef(({ className, children, ...props }, ref) => /* @__PURE__ */ (0, import_jsx_runtime.jsxs)(DialogPortal, { children: [/* @__PURE__ */ (0, import_jsx_runtime.jsx)(DialogOverlay, {}), /* @__PURE__ */ (0, import_jsx_runtime.jsxs)(DialogContent$1, {
	ref,
	className: cn("fixed left-[50%] top-[50%] z-50 grid w-full max-w-lg translate-x-[-50%] translate-y-[-50%] gap-4 border bg-background p-6 shadow-lg duration-200 data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 data-[state=closed]:zoom-out-95 data-[state=open]:zoom-in-95 sm:rounded-lg", className),
	...props,
	children: [children, /* @__PURE__ */ (0, import_jsx_runtime.jsxs)(DialogClose, {
		className: "absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background cursor-pointer transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:pointer-events-none data-[state=open]:bg-accent data-[state=open]:text-muted-foreground",
		children: [/* @__PURE__ */ (0, import_jsx_runtime.jsx)(X, { className: "h-4 w-4" }), /* @__PURE__ */ (0, import_jsx_runtime.jsx)("span", {
			className: "sr-only",
			children: "Close"
		})]
	})]
})] }));
DialogContent.displayName = DialogContent$1.displayName;
var DialogHeader = ({ className, ...props }) => /* @__PURE__ */ (0, import_jsx_runtime.jsx)("div", {
	className: cn("flex flex-col space-y-1.5 text-center sm:text-left", className),
	...props
});
DialogHeader.displayName = "DialogHeader";
var DialogFooter = ({ className, ...props }) => /* @__PURE__ */ (0, import_jsx_runtime.jsx)("div", {
	className: cn("flex flex-col-reverse sm:flex-row sm:justify-end sm:space-x-2", className),
	...props
});
DialogFooter.displayName = "DialogFooter";
var DialogTitle = import_react.forwardRef(({ className, ...props }, ref) => /* @__PURE__ */ (0, import_jsx_runtime.jsx)(DialogTitle$1, {
	ref,
	className: cn("text-lg font-semibold leading-none tracking-tight", className),
	...props
}));
DialogTitle.displayName = DialogTitle$1.displayName;
var DialogDescription = import_react.forwardRef(({ className, ...props }, ref) => /* @__PURE__ */ (0, import_jsx_runtime.jsx)(DialogDescription$1, {
	ref,
	className: cn("text-sm text-muted-foreground", className),
	...props
}));
DialogDescription.displayName = DialogDescription$1.displayName;
//#endregion
export { userQuery as S, leetcodeQuery as _, DialogDescription as a, skillEvidenceQuery as b, DialogTitle as c, careerQuery as d, cn as f, githubAnalyticsQuery as g, githubActivityQuery as h, DialogContent as i, DialogTrigger as l, getAccessToken as m, Button as n, DialogFooter as o, dsaQuery as p, Dialog as r, DialogHeader as s, ApiError as t, Skeleton as u, projectsQuery as v, skillsQuery as x, setAccessToken as y };

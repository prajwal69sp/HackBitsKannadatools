import { NextRequest, NextResponse } from "next/server";
import { allowedApiOrigins, isAllowedApiOrigin } from "@/lib/cors";

const methods = "GET, POST, OPTIONS";
const headers = "Accept, Authorization, Content-Type";

export function proxy(request: NextRequest) {
  const origin = request.headers.get("origin");
  if (!origin) {
    if (request.method === "OPTIONS") return new NextResponse(null, { status: 204, headers: { Allow: methods } });
    return NextResponse.next();
  }

  const allowedOrigins = allowedApiOrigins();
  if (!isAllowedApiOrigin(origin, allowedOrigins)) {
    return NextResponse.json({ success: false, error: "Origin is not allowed." }, { status: 403 });
  }

  const allowedMethods = request.nextUrl.pathname.endsWith("/events") ? methods : "GET, OPTIONS";
  if (request.method === "OPTIONS") {
    const requestedMethod = request.headers.get("access-control-request-method")?.toUpperCase();
    if (requestedMethod && !allowedMethods.split(", ").includes(requestedMethod)) {
      return NextResponse.json({ success: false, error: "Method is not allowed." }, { status: 405 });
    }
    const requestedHeaders = request.headers.get("access-control-request-headers");
    if (requestedHeaders && requestedHeaders.split(",").some((header) =>
      !headers.split(", ").some((allowed) => allowed.toLowerCase() === header.trim().toLowerCase()),
    )) {
      return NextResponse.json({ success: false, error: "Request headers are not allowed." }, { status: 403 });
    }
    return new NextResponse(null, {
      status: 204,
      headers: {
        "Access-Control-Allow-Origin": origin,
        "Access-Control-Allow-Methods": allowedMethods,
        "Access-Control-Allow-Headers": headers,
        "Access-Control-Max-Age": "600",
        Vary: "Origin",
      },
    });
  }

  const response = NextResponse.next();
  response.headers.set("Access-Control-Allow-Origin", origin);
  response.headers.set("Access-Control-Allow-Methods", allowedMethods);
  response.headers.set("Access-Control-Allow-Headers", headers);
  response.headers.set("Access-Control-Max-Age", "600");
  response.headers.set("Vary", "Origin");
  return response;
}

export const config = {
  matcher: ["/api/v1/:path*"],
};

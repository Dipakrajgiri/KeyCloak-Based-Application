import type { PageProps } from "keycloakify/login/pages/PageProps";
import type { KcContext } from "../KcContext";
import type { I18n } from "../i18n";

export default function LoginPageExpired(props: PageProps<Extract<KcContext, { pageId: "login-page-expired.ftl" }>, I18n>) {
    const { kcContext, i18n } = props;
    const { url } = kcContext;
    const { msg } = i18n;

    return (
        <>
            <div className="logo-container">
                <div className="logo-icon" style={{ background: "linear-gradient(135deg, #ffb347 0%, #ff6b6b 100%)" }}>
                    <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"/>
                    </svg>
                </div>
                <h1 className="page-title">Page Expired</h1>
                <p className="page-subtitle">Your session has timed out for security</p>
            </div>

            <div className="alert alert-warning" style={{ marginBottom: "24px" }}>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
                </svg>
                <span>{msg("pageExpiredMsg1")}</span>
            </div>

            <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
                <a href={url.loginRestartFlowUrl} className="btn-primary" style={{ display: "flex", alignItems: "center", justifyContent: "center", textDecoration: "none" }}>
                    Restart Login
                </a>
                <a href={url.loginAction} className="btn-secondary" style={{ textDecoration: "none" }}>
                    Continue Where You Left Off
                </a>
            </div>
        </>
    );
}

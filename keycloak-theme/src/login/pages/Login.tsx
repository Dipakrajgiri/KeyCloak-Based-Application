import { useState, type FormEventHandler } from "react";
import type { PageProps } from "keycloakify/login/pages/PageProps";
import type { KcContext } from "../KcContext";
import type { I18n } from "../i18n";

export default function Login(props: PageProps<Extract<KcContext, { pageId: "login.ftl" }>, I18n>) {
    const { kcContext, i18n } = props;
    const { social, realm, url, usernameHidden, login, registrationDisabled, messagesPerField } = kcContext;
    const { msg } = i18n;

    const [isLoginButtonDisabled, setIsLoginButtonDisabled] = useState(false);

    const onSubmit: FormEventHandler<HTMLFormElement> = () => {
        setIsLoginButtonDisabled(true);
        return true;
    };

    const label = !realm.loginWithEmailAllowed
        ? "username"
        : realm.registrationEmailAsUsername
          ? "email"
          : "usernameOrEmail";

    return (
        <>
            {/* Header */}
            <div className="logo-container">
                <div className="logo-icon">
                    <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M20 7h-4V4c0-1.1-.9-2-2-2h-4c-1.1 0-2 .9-2 2v3H4c-1.1 0-2 .9-2 2v11c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V9c0-1.1-.9-2-2-2zM10 4h4v3h-4V4zm6 11h-3v3c0 .55-.45 1-1 1s-1-.45-1-1v-3H8c-.55 0-1-.45-1-1s.45-1 1-1h3v-3c0-.55.45-1 1-1s1 .45 1 1v3h3c.55 0 1 .45 1 1s-.45 1-1 1z"/>
                    </svg>
                </div>
                <h1 className="page-title">Welcome Back</h1>
                <p className="page-subtitle">Sign in to your account to continue</p>
            </div>

            {/* Social Providers */}
            {realm.password && social?.providers !== undefined && social.providers.length > 0 && (
                <>
                    <div className="social-buttons">
                        {social.providers.map(p => (
                            <a key={p.providerId} href={p.loginUrl} className="social-btn" title={p.displayName}>
                                <span dangerouslySetInnerHTML={{ __html: p.iconClasses || p.displayName }} />
                            </a>
                        ))}
                    </div>
                    <div className="divider"><span>or continue with email</span></div>
                </>
            )}

            {/* Login Form */}
            {realm.password && (
                <form onSubmit={onSubmit} action={url.loginAction} method="post">
                    {!usernameHidden && (
                        <div className="form-group">
                            <label className="form-label" htmlFor="username">{msg(label)}</label>
                            <div className="input-icon-wrapper">
                                <input
                                    id="username"
                                    name="username"
                                    defaultValue={login.username ?? ""}
                                    type="text"
                                    autoFocus
                                    autoComplete="username"
                                    className={`form-input ${messagesPerField.existsError("username", "usernameOrEmail") ? "has-error" : ""}`}
                                    placeholder={label === "email" ? "name@company.com" : "Enter your username or email"}
                                    aria-invalid={messagesPerField.existsError("username", "usernameOrEmail")}
                                />
                                <span className="input-icon">
                                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
                                    </svg>
                                </span>
                            </div>
                            {messagesPerField.existsError("username", "usernameOrEmail") && (
                                <div className="field-error" aria-live="polite">{messagesPerField.getFirstError("username", "usernameOrEmail")}</div>
                            )}
                        </div>
                    )}

                    <div className="form-group">
                        <label className="form-label" htmlFor="password">{msg("password")}</label>
                        <div className="input-icon-wrapper">
                            <input
                                id="password"
                                name="password"
                                type="password"
                                autoComplete="current-password"
                                className={`form-input ${messagesPerField.existsError("password") ? "has-error" : ""}`}
                                placeholder="Enter your password"
                                aria-invalid={messagesPerField.existsError("password")}
                            />
                            <span className="input-icon">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                                </svg>
                            </span>
                        </div>
                        {messagesPerField.existsError("password") && (
                            <div className="field-error" aria-live="polite">{messagesPerField.getFirstError("password")}</div>
                        )}
                    </div>

                    {/* Remember me & Forgot */}
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "24px" }}>
                        {realm.rememberMe && !usernameHidden && (
                            <div className="remember-me" style={{ marginBottom: 0 }}>
                                <input type="checkbox" id="rememberMe" name="rememberMe" defaultChecked={!!login.rememberMe} />
                                <label htmlFor="rememberMe">{msg("rememberMe")}</label>
                            </div>
                        )}
                        {realm.resetPasswordAllowed && (
                            <a href={url.loginResetCredentialsUrl} className="link">{msg("doForgotPassword")}</a>
                        )}
                    </div>

                    <input type="hidden" id="id-hidden-input" name="credentialId" value={kcContext.auth?.selectedCredential} />

                    <button type="submit" className="btn-primary" disabled={isLoginButtonDisabled}>
                        {isLoginButtonDisabled ? <span className="spinner" /> : msg("doLogIn")}
                    </button>
                </form>
            )}

            {/* Register Link */}
            {realm.password && realm.registrationAllowed && !registrationDisabled && (
                <div className="card-footer">
                    <span>Don't have an account?</span>
                    <a href={url.registrationUrl} className="link">Create one</a>
                </div>
            )}
        </>
    );
}

import { useState, type FormEventHandler } from "react";
import type { PageProps } from "keycloakify/login/pages/PageProps";
import type { KcContext } from "../KcContext";
import type { I18n } from "../i18n";

export default function LoginUpdatePassword(props: PageProps<Extract<KcContext, { pageId: "login-update-password.ftl" }>, I18n>) {
    const { kcContext, i18n } = props;
    const { url, messagesPerField, isAppInitiatedAction } = kcContext;
    const { msg } = i18n;

    const [isSubmitting, setIsSubmitting] = useState(false);

    const onSubmit: FormEventHandler<HTMLFormElement> = () => {
        setIsSubmitting(true);
        return true;
    };

    return (
        <>
            <div className="logo-container">
                <div className="logo-icon">
                    <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12.65 10C11.83 7.67 9.61 6 7 6c-3.31 0-6 2.69-6 6s2.69 6 6 6c2.61 0 4.83-1.67 5.65-4H17v4h4v-4h2v-4H12.65zM7 14c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2z"/>
                    </svg>
                </div>
                <h1 className="page-title">Update Password</h1>
                <p className="page-subtitle">Choose a strong, unique password</p>
            </div>

            <form onSubmit={onSubmit} action={url.loginAction} method="post">
                <div className="form-group">
                    <label className="form-label" htmlFor="password-new">{msg("passwordNew")}</label>
                    <div className="input-icon-wrapper">
                        <input
                            id="password-new"
                            name="password-new"
                            type="password"
                            autoFocus
                            autoComplete="new-password"
                            className={`form-input ${messagesPerField.existsError("password") ? "has-error" : ""}`}
                            placeholder="Enter new password"
                        />
                        <span className="input-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                            </svg>
                        </span>
                    </div>
                    {messagesPerField.existsError("password") && (
                        <div className="field-error">{messagesPerField.getFirstError("password")}</div>
                    )}
                </div>

                <div className="form-group">
                    <label className="form-label" htmlFor="password-confirm">{msg("passwordConfirm")}</label>
                    <div className="input-icon-wrapper">
                        <input
                            id="password-confirm"
                            name="password-confirm"
                            type="password"
                            autoComplete="new-password"
                            className={`form-input ${messagesPerField.existsError("password-confirm") ? "has-error" : ""}`}
                            placeholder="Confirm new password"
                        />
                        <span className="input-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
                            </svg>
                        </span>
                    </div>
                    {messagesPerField.existsError("password-confirm") && (
                        <div className="field-error">{messagesPerField.getFirstError("password-confirm")}</div>
                    )}
                </div>

                <div style={{ display: "flex", gap: "12px", marginTop: "8px" }}>
                    <button type="submit" className="btn-primary" disabled={isSubmitting}>
                        {isSubmitting ? <span className="spinner" /> : "Update Password"}
                    </button>
                    {isAppInitiatedAction && (
                        <button type="submit" name="cancel-aia" value="true" className="btn-secondary" style={{ flex: "0 0 auto", width: "auto", padding: "0 24px" }}>
                            Cancel
                        </button>
                    )}
                </div>
            </form>
        </>
    );
}

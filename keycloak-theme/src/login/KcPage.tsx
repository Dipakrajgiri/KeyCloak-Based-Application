import { Suspense, lazy } from "react";
import type { ClassKey } from "keycloakify/login";
import type { KcContext } from "./KcContext";
import { useI18n } from "./i18n";
import DefaultPage from "keycloakify/login/DefaultPage";
import Template from "./Template";

const Login = lazy(() => import("./pages/Login"));
const Register = lazy(() => import("./pages/Register"));
const LoginResetPassword = lazy(() => import("./pages/LoginResetPassword"));
const LoginUpdatePassword = lazy(() => import("./pages/LoginUpdatePassword"));
const ErrorPage = lazy(() => import("./pages/Error"));
const Info = lazy(() => import("./pages/Info"));
const LoginPageExpired = lazy(() => import("./pages/LoginPageExpired"));

const UserProfileFormFields = lazy(
    () => import("keycloakify/login/UserProfileFormFields")
);

const doMakeUserConfirmPassword = true;

export default function KcPage(props: { kcContext: KcContext }) {
    const { kcContext } = props;
    const { i18n } = useI18n({ kcContext });

    return (
        <Suspense>
            {(() => {
                switch (kcContext.pageId) {
                    case "login.ftl":
                        return (
                            <Template kcContext={kcContext} i18n={i18n} doUseDefaultCss={false} classes={classes} headerNode={<></>}>
                                <Login kcContext={kcContext} i18n={i18n} doUseDefaultCss={false} classes={classes} Template={Template} />
                            </Template>
                        );
                    case "register.ftl":
                        return (
                            <Template kcContext={kcContext} i18n={i18n} doUseDefaultCss={false} classes={classes} headerNode={<></>}>
                                <Register kcContext={kcContext} i18n={i18n} doUseDefaultCss={false} classes={classes} Template={Template} />
                            </Template>
                        );
                    case "login-reset-password.ftl":
                        return (
                            <Template kcContext={kcContext} i18n={i18n} doUseDefaultCss={false} classes={classes} headerNode={<></>}>
                                <LoginResetPassword kcContext={kcContext} i18n={i18n} doUseDefaultCss={false} classes={classes} Template={Template} />
                            </Template>
                        );
                    case "login-update-password.ftl":
                        return (
                            <Template kcContext={kcContext} i18n={i18n} doUseDefaultCss={false} classes={classes} headerNode={<></>}>
                                <LoginUpdatePassword kcContext={kcContext} i18n={i18n} doUseDefaultCss={false} classes={classes} Template={Template} />
                            </Template>
                        );
                    case "error.ftl":
                        return (
                            <Template kcContext={kcContext} i18n={i18n} doUseDefaultCss={false} classes={classes} headerNode={<></>} displayMessage={false}>
                                <ErrorPage kcContext={kcContext} i18n={i18n} doUseDefaultCss={false} classes={classes} Template={Template} />
                            </Template>
                        );
                    case "info.ftl":
                        return (
                            <Template kcContext={kcContext} i18n={i18n} doUseDefaultCss={false} classes={classes} headerNode={<></>} displayMessage={false}>
                                <Info kcContext={kcContext} i18n={i18n} doUseDefaultCss={false} classes={classes} Template={Template} />
                            </Template>
                        );
                    case "login-page-expired.ftl":
                        return (
                            <Template kcContext={kcContext} i18n={i18n} doUseDefaultCss={false} classes={classes} headerNode={<></>} displayMessage={false}>
                                <LoginPageExpired kcContext={kcContext} i18n={i18n} doUseDefaultCss={false} classes={classes} Template={Template} />
                            </Template>
                        );
                    default:
                        return (
                            <DefaultPage
                                kcContext={kcContext}
                                i18n={i18n}
                                classes={classes}
                                Template={Template}
                                doUseDefaultCss={true}
                                UserProfileFormFields={UserProfileFormFields}
                                doMakeUserConfirmPassword={doMakeUserConfirmPassword}
                            />
                        );
                }
            })()}
        </Suspense>
    );
}

const classes = {} satisfies { [key in ClassKey]?: string };

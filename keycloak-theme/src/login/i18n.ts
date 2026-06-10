/* eslint-disable @typescript-eslint/no-unused-vars */
import { i18nBuilder } from "keycloakify/login";
import type { ThemeName } from "../kc.gen";

/** @see: https://docs.keycloakify.dev/features/i18n */
const { useI18n, ofTypeI18n } = i18nBuilder
    .withThemeName<ThemeName>()
    .withExtraLanguages({})
    .withCustomTranslations({
        en: {
            loginTitle: "Welcome Back",
            registerTitle: "Create Account",
            resetPasswordTitle: "Reset Password",
            appName: "SSO Inventory",
            appTagline: "Manage your inventory seamlessly",
            loginSubtitle: "Sign in to your account to continue",
            registerSubtitle: "Join us and start managing your inventory",
            resetSubtitle: "Enter your email to receive a reset link",
            orContinueWith: "or continue with"
        }
    })
    .build();

type I18n = typeof ofTypeI18n;

export { useI18n, type I18n };

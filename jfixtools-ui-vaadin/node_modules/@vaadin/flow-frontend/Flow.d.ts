export interface FlowConfig {
    imports?: () => void;
}
interface AppConfig {
    productionMode: boolean;
    appId: string;
    uidl: object;
    webComponentMode: boolean;
}
interface AppInitResponse {
    appConfig: AppConfig;
    pushScript?: string;
}
interface HTMLRouterContainer extends HTMLElement {
    onBeforeEnter?: (ctx: NavigationParameters, cmd: NavigationCommands) => Promise<any>;
    onBeforeLeave?: (ctx: NavigationParameters, cmd: NavigationCommands) => Promise<any>;
    serverConnected?: (cancel: boolean) => void;
}
interface FlowRoute {
    action: (params: NavigationParameters) => Promise<HTMLRouterContainer>;
    path: string;
}
export interface NavigationParameters {
    pathname: string;
    search: string;
}
export interface NavigationCommands {
    prevent: () => any;
}
/**
 * Client API for flow UI operations.
 */
export declare class Flow {
    config: FlowConfig;
    response?: AppInitResponse;
    pathname: string;
    container: HTMLRouterContainer;
    private isActive;
    private baseRegex;
    constructor(config?: FlowConfig);
    /**
     * Return a `route` object for vaadin-router in an one-element array.
     *
     * The `FlowRoute` object `path` property handles any route,
     * and the `action` returns the flow container without updating the content,
     * delaying the actual Flow server call to the `onBeforeEnter` phase.
     *
     * This is a specific API for its use with `vaadin-router`.
     */
    get serverSideRoutes(): [FlowRoute];
    private get action();
    private flowLeave;
    private flowNavigate;
    private getFlowRoute;
    private flowInit;
    private loadScript;
    private injectAppIdScript;
    private flowInitClient;
    private flowInitUi;
}
export {};

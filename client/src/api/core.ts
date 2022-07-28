export async function fetchJSON<T>(input: RequestInfo, init?: RequestInit | undefined): Promise<T> {
    const response = await fetch(input, init);
    if (!response.ok) {
        if (response.status === 401) {
            debugger;
            window.location.href = '/api/oauth2/authorization/withings-client';
        }
        throw new Error(await response.text());
    }

    return await response.json();
}
from typing import Optional

from constants import analytics_url, analytics_app_name, analytics_app_id
from log_constructor import construct_log
from dataclasses import dataclass
import requests

logger = construct_log("send_analytics")


@dataclass
class AnalyticsRequestFields:
    app_id: Optional[int] = None
    event: Optional[str] = None
    event_params: Optional[dict] = None
    app_name: Optional[str] = None
    userId: Optional[str] = None
    element: Optional[str] = None


def _is_valid_data(data: AnalyticsRequestFields) -> bool:
    # print(vars(data))
    for key, value in vars(data).items():
        if value is None:
            return False
    return True


def _send_enriched_analytics(data: AnalyticsRequestFields) -> None:
    if not _is_valid_data(data):
        logger.warning("Incorrect fields in analytics data")
        return
    headers = {"Accept": "application/json", "Content-Type": "text/plain"}
    params = vars(data)
    response = requests.post(analytics_url, headers=headers, json=params)
    if response.ok:
        logger.info("Send trigger to analytics db")
    else:
        logger.warning("Failed to send trigger to analytics db")


async def send_analytics(*, event: Optional[str] = None, event_params: Optional[dict] = None,
                         user_id: Optional[str] = None, element: Optional[str] = None):
    if event_params is None:
        event_params = dict()
    data = AnalyticsRequestFields(
        app_id=analytics_app_id, event=event, event_params=event_params, app_name=analytics_app_name,
        userId=user_id, element=element
    )
    _send_enriched_analytics(data)


if __name__ == "__main__":
    # data = AnalyticsRequestFields()
    # data.app_id = 1
    # data.event = "telegram-button-click"
    # data.event_params = {"some-info": [1, 2, 3, 4, 5]}
    # data.app_name = "abacaba"
    # data.userId = "user-id-3"
    # data.element = "btn btn-light btn-search"
    #
    # send_analytics(data)
    pass

